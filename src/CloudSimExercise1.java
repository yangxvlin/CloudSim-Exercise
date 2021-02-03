import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.lists.PeList;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisioner;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

/**
 * Xulin Yang, 904904
 *
 * @create 2021-02-03 15:35
 * description:
 **/

public class CloudSimExercise1 {
    /** The cloudlet list. */
    private static List<Cloudlet> cloudletList;
    /** The vmlist. */
    private static List<Vm> vmlist;

    public static void main(String[] args) {
        Log.printLine("Starting CloudSimExercise1...");

        try {
            // step 1.0: Initialize the CloudSim package. It should be called before creating any entities.
            int numUsers = 1;
            Calendar calendar = Calendar.getInstance();
            CloudSim.init(numUsers, calendar, false);

            // step 2.0: Create Datacenter(s)
            //     (Datacenter <<-- Datacentercharacteristics <<-- HostList <<-- Processing element List)
            // (Defines policy for VM allocation and scheduling)
            Datacenter datacenter = createDatacenter("Datacenter_0");

            // step 3.0: Create Broker
            DatacenterBroker datacenterBroker = createBroker();
            int userID = datacenterBroker.getId();

            // step 4.0: Create Cloudlets(Defines the workload)
            // 40 Cloudlets(tasks/workload)
            //      40000 length of instructions
            //      300 kb input filesize
            //      400 kb output filesize
            //      1 core cpu
            //      utilization model to full
            cloudletList = new ArrayList<>();
            int length = 40000;
            long inputFileSize = 300000;
            long outputFileSize = 400000;
            int peNumber = 1;
            UtilizationModel utilizationModel = new UtilizationModelFull();
            for (int cloudletID = 0; cloudletID < 40; cloudletID++) {
                Cloudlet cloudlet = new Cloudlet(
                        cloudletID,
                        length,
                        peNumber,
                        inputFileSize,
                        outputFileSize,
                        utilizationModel,
                        utilizationModel,
                        utilizationModel
                );
                cloudlet.setUserId(userID);
                cloudletList.add(cloudlet);
            }
            // submit cloudlet list to the broker
            datacenterBroker.submitCloudletList(cloudletList);

            // step 5.0: Create VMs(Define the procedure for Task scheduling algorithm)
            // 10 Virtual machines
            //      20 GB Storage disk
            //      2 GB RAM
            //      1 vCPU with 1000 mips CPU speed
            //      1000 kbits/s Bandwidth
            //      Timeshared scheduler for cloudlets execution
            vmlist = new ArrayList<>();
            long size = 20 * 1024;
            int ram = 2 * 1024;
            double mips = 1000;
            long bw = 1000;
            String vmm = "Xen";
            for (int vmID = 0; vmID < 10; vmID++) {
                vmlist.add(new Vm(
                        vmID,
                        userID,
                        mips,
                        peNumber,
                        ram,
                        bw,
                        size,
                        vmm,
                        new CloudletSchedulerTimeShared()
                ));
            }
            // submit vm list to the broker
            datacenterBroker.submitVmList(vmlist);

            // step 6.0: Starts the simulation(automatad process, handled through descreted event simulation_engins)
            CloudSim.startSimulation();
            CloudSim.stopSimulation();

            // step 7.0: Print results when simulation is over(Outputs)
            List<Cloudlet> newList = datacenterBroker.getCloudletReceivedList();
            // printCloudletList(newList);
            int cloudNo = 0;
            for (Cloudlet c: newList) {
                Log.printLine("******************************");
                Log.printLine("Result of cloud No: " + cloudNo);
                Log.printLine("ID:"+c.getCloudletId() +
                              ", VM:"+c.getVmId() +
                              ", status: " + c.getStatus() +
                              ", Execution time:" + c.getActualCPUTime() +
                              ", Start:" + c.getExecStartTime() +
                              ", End:" + c.getFinishTime()
                             );
                cloudNo++;
            }
            // Note: The execution is 160 unit of time in the result because the
            //       VM has CloudletSchedulerTimeShared which results 4 cloudlets
            //       are executed concurrently on same VM. In other words, if
            //       only 1 cloudlet on VM, it uses 40000/1000 = 40 unit of time.
            //       As 4 concurrent cloudlet, single cloudlet uses 40*4=160
            //       unit of time now.

        } catch (Exception e) {
            e.printStackTrace();
            Log.printLine("Unwanted errors happen");
        }
    }

    /**
     * Creates the datacenter.
     *
     * @param name the name
     *
     * @return the datacenter
     */
    private static Datacenter createDatacenter(String name) {
        // (Datacenter <<-- Datacentercharacteristics <<-- HostList <<-- Processing element List)

        // 4 Host with 4 Pe
        //      1000 mips
        double mips = 1000;
        List<Pe> peList = new ArrayList<>();
        PeProvisioner peProvisioner = new PeProvisionerSimple(mips);
        for (int peID = 0; peID < 4; peID++) {
            peList.add(new Pe(peID, peProvisioner));
        }

        // 4 Host
        //      8GB RAM
        //      100GB storage = 100000MB
        //      1 mbps(8000 Kbits/s) network bandwidth(measured as Kbits/s)
        int ram = 8 * 1024;
        long bw = 8000;
        long storage = 100000;
        List<Host> hostList = new ArrayList<>();
        for (int hostID = 0; hostID < 4; hostID++) {
            hostList.add(new Host(hostID,
                                  new RamProvisionerSimple(ram),
                                  new BwProvisionerSimple(bw),
                                  storage,
                                  peList,
                                  new VmSchedulerSpaceShared(peList)
                    ));
        }

        String arch = "x86"; // system architecture
        String os = "Linux"; // operating system
        String vmm = "Xen";
        double time_zone = 5.0; // time zone this resource located
        double cost = 3.0; // the cost of using processing in this resource
        double costPerMem = 1.0; // the cost of using memory in this resource
        double costPerStorage = 0.05; // the cost of using storage in this resource
        double costPerBw = 0.10; // the cost of using bw in this resource
        DatacenterCharacteristics dc = new DatacenterCharacteristics(
                arch,
                os,
                vmm,
                hostList,
                time_zone,
                cost,
                costPerMem,
                costPerStorage,
                costPerBw
        );

        LinkedList<Storage> storageList = new LinkedList<Storage>(); // we are not adding SAN devices by now
        Datacenter datacenter = null;
        try {
            datacenter = new Datacenter(name,
                                        dc,
                                        new VmAllocationPolicySimple(hostList),
                                        storageList,
                                        0
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return datacenter;
    }

    /**
     * Creates the broker.
     *
     * @return the datacenter broker
     */
    private static DatacenterBroker createBroker() {
        DatacenterBroker broker = null;
        try {
            broker = new DatacenterBroker("Broker");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return broker;
    }

    /**
     * Prints the Cloudlet objects.
     *
     * @param list list of Cloudlets
     */
    private static void printCloudletList(List<Cloudlet> list) {
        int size = list.size();
        Cloudlet cloudlet;

        String indent = "    ";
        Log.printLine();
        Log.printLine("========== OUTPUT ==========");
        Log.printLine("Cloudlet ID" + indent + "STATUS" + indent
                + "Data center ID" + indent + "VM ID" + indent + "Time" + indent
                + "Start Time" + indent + "Finish Time");

        DecimalFormat dft = new DecimalFormat("###.##");
        for (int i = 0; i < size; i++) {
            cloudlet = list.get(i);
            Log.print(indent + cloudlet.getCloudletId() + indent + indent);

            if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS) {
                Log.print("SUCCESS");

                Log.printLine(indent + indent + cloudlet.getResourceId()
                        + indent + indent + indent + cloudlet.getVmId()
                        + indent + indent
                        + dft.format(cloudlet.getActualCPUTime()) + indent
                        + indent + dft.format(cloudlet.getExecStartTime())
                        + indent + indent
                        + dft.format(cloudlet.getFinishTime()));
            }
        }
    }
}
