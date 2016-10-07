package io.github.retz.scheduler;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.mesos.MesosSchedulerDriver;
import org.apache.mesos.Protos;
import org.apache.mesos.Scheduler;
import org.apache.mesos.SchedulerDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;

public class SchedulerDriverFactory {
    private static final Logger LOG = LoggerFactory.getLogger(SchedulerDriverFactory.class);


    private SchedulerDriverFactory() {
    }

    public static SchedulerDriver create(Scheduler scheduler, Launcher.Configuration conf, Protos.FrameworkInfo fw) {
        try {
            switch (conf.launchMode) {
                case LOCAL:
                    return new LocalSchedulerDriver(scheduler, fw, conf.getMesosMaster());
                case MESOS:
                    return createMesosSchedulerDriver(scheduler, conf, fw);
            }
        } catch (InvalidProtocolBufferException e) {
            LOG.error(e.toString());
        } catch (IOException e) {
            LOG.error("Cannot read secret file: {}", e.toString());
        }

        throw new RuntimeException("No suitable scheduler found or initialization failed.");
    }

    static MesosSchedulerDriver createMesosSchedulerDriver(Scheduler scheduler, Launcher.Configuration conf, Protos.FrameworkInfo fw)
            throws IOException {
        Protos.Credential credential = null;

        if (conf.fileConfig.hasSecretFile()) {
            credential = Protos.Credential.newBuilder()
                    .setPrincipal(conf.fileConfig.getPrincipal())
                    .setSecretBytes(ByteString.readFrom(new FileInputStream(conf.fileConfig.getSecretFile())))
                    .build();
        }

        LOG.info("{} starting", MesosSchedulerDriver.class.getName());
        if (credential != null) {
            return new MesosSchedulerDriver(scheduler, fw, conf.getMesosMaster(), credential);
        }
        return new MesosSchedulerDriver(scheduler, fw, conf.getMesosMaster());
    }
}
