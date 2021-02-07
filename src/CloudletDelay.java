import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.UtilizationModel;

import java.util.List;

/**
 * Xulin Yang, 904904
 *
 * @create 2021-02-07 15:39
 * description:
 **/

public class CloudletDelay extends Cloudlet {
    private int delayCloudlet;

    public CloudletDelay(int cloudletId, long cloudletLength, int pesNumber, long cloudletFileSize, long cloudletOutputSize,
                         UtilizationModel utilizationModelCpu, UtilizationModel utilizationModelRam, UtilizationModel utilizationModelBw,
                         int delayCloudlet) {
        super(cloudletId, cloudletLength, pesNumber, cloudletFileSize, cloudletOutputSize, utilizationModelCpu, utilizationModelRam, utilizationModelBw);
        this.delayCloudlet = delayCloudlet;
    }

    public CloudletDelay(int cloudletId, long cloudletLength, int pesNumber, long cloudletFileSize, long cloudletOutputSize,
                         UtilizationModel utilizationModelCpu, UtilizationModel utilizationModelRam, UtilizationModel utilizationModelBw,
                         boolean record, List<String> fileList) {
        super(cloudletId, cloudletLength, pesNumber, cloudletFileSize, cloudletOutputSize, utilizationModelCpu, utilizationModelRam, utilizationModelBw, record, fileList);
    }

    public CloudletDelay(int cloudletId, long cloudletLength, int pesNumber, long cloudletFileSize, long cloudletOutputSize,
                         UtilizationModel utilizationModelCpu, UtilizationModel utilizationModelRam, UtilizationModel utilizationModelBw,
                         List<String> fileList) {
        super(cloudletId, cloudletLength, pesNumber, cloudletFileSize, cloudletOutputSize, utilizationModelCpu, utilizationModelRam, utilizationModelBw, fileList);
    }

    public CloudletDelay(int cloudletId, long cloudletLength, int pesNumber, long cloudletFileSize, long cloudletOutputSize,
                         UtilizationModel utilizationModelCpu, UtilizationModel utilizationModelRam, UtilizationModel utilizationModelBw,
                         boolean record) {
        super(cloudletId, cloudletLength, pesNumber, cloudletFileSize, cloudletOutputSize, utilizationModelCpu, utilizationModelRam, utilizationModelBw, record);
    }

    public int getDelayCloudlet() {
        return delayCloudlet;
    }
}
