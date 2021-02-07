import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisioner;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

import java.text.DecimalFormat;
import java.util.*;

/**
 * Xulin Yang, 904904
 *
 * @create 2021-02-05 13:15
 * description: how do I create 5 datacenter with 2 hosts in each data center
 *              and moreover 2 cloudlets will run on each host
 **/

public class CloudSimExercise3 {
    /** The cloudlet list. */
    private static List<Cloudlet> cloudletList;
    /** The vmlist. */
    private static List<Vm> vmlist;

    public static void main(String[] args) {
        // The following steps can be followed:
        //      1. Create a new Class in org.cloudbus.cloudsim.examples package.
        //      2. (To save time) Make a copy of the code from any basic Example class.
        int numUsers = 1;
        Calendar calendar = Calendar.getInstance();
        CloudSim.init(numUsers, calendar, false);
        //      3. Add more instance of Datacenter and do check your host count and configuration specially the RAM
        //          I am assuming to have each host with dualcore processor and 2048 MB(2gb) RAM
        List<Datacenter> datacenterList = new ArrayList<>();
        for (int datacenterID = 0; datacenterID < 5; datacenterID++) {
            Datacenter datacenter = createDatacenter("Datacenter_" + datacenterID);
            datacenterList.add(datacenter);
        }
        DatacenterBroker datacenterBroker = createBroker();
        int userID = datacenterBroker.getId();
        //      4. Add more instances of virtual machine where each virtual machine will be allocated to each indivial host. Please keep in mind that for the cloudlet execution are done through virtual machines not the hosts
        //          for 5 datacenter total no of hosts will be 10. the number of virtual machines to be used for this scenario will be 10.
        vmlist = new ArrayList<>();
        long size = 20 * 1024;
        int peNumber = 1;
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
                    new CloudletSchedulerSpaceShared()
            ));
        }
        //      5. Add more instances of cloudlets and required to bind the cloudlets to to a particular virtual machine.
        //          I am assuming 2 cloudlets on one single host(vm) therefore total number of cloudlets should be 10 vm x 2 = 20 cloudlets
        cloudletList = new ArrayList<>();
        int length = 40000;
        long inputFileSize = 300000;
        long outputFileSize = 400000;
        UtilizationModel utilizationModel = new UtilizationModelFull();
        Random random = new Random();
        for (int cloudletID = 0; cloudletID < 20; cloudletID++) {
            Cloudlet cloudlet = new Cloudlet(
                    cloudletID,
//                    length + random.nextInt(100),
                    length,
                    peNumber,
                    inputFileSize,
                    outputFileSize,
                    utilizationModel,
                    utilizationModel,
                    utilizationModel
            );
            cloudlet.setUserId(userID);
            cloudlet.setVmId(cloudletID / 2);
            cloudletList.add(cloudlet);
        }
        //      6. Bind the datacenterbroker with appropriate VM lis as cloudlet list
        datacenterBroker.submitVmList(vmlist);
        datacenterBroker.submitCloudletList(cloudletList);
        //      7. Test your implemented changes by verifying the output.
        CloudSim.startSimulation();
        CloudSim.stopSimulation();
        List<Cloudlet> newList = datacenterBroker.getCloudletReceivedList();
        printCloudletList(newList);
//        int cloudNo = 0;
//        for (Cloudlet c: newList) {
//            Log.printLine("******************************");
//            Log.printLine("Result of cloud No: " + cloudNo);
//            Log.printLine("ID:"+c.getCloudletId() +
//                          ", resource:"+c.getResourceName(c.getResourceId()) +
//                          ", VM:"+c.getVmId() +
//                          ", status:" + c.getStatus() +
//                          ", Execution time:" + c.getActualCPUTime() +
//                          ", Start:" + c.getExecStartTime() +
//                          ", End:" + c.getFinishTime()
//            );
//            cloudNo++;
//        }
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

        // 2 Host with dual Pe
        //      1000 mips
        double mips = 1000;
        List<Pe> peList = new ArrayList<>();
        PeProvisioner peProvisioner = new PeProvisionerSimple(mips);
        for (int peID = 0; peID < 2; peID++) {
            peList.add(new Pe(peID, peProvisioner));
        }

        // 2 Host
        //      2GB RAM
        //      100GB storage = 100000MB
        //      1 mbps(8000 Kbits/s) network bandwidth(measured as Kbits/s)
        int ram = 2 * 1024;
        long bw = 8000;
        long storage = 100000;
        List<Host> hostList = new ArrayList<>();
        for (int hostID = 0; hostID < 2; hostID++) {
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
