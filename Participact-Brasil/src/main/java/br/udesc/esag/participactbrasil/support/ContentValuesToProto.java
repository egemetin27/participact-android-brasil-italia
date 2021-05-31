package br.udesc.esag.participactbrasil.support;

import android.content.ContentValues;
import android.util.Log;

import org.most.pipeline.PipelineAccelerometer;
import org.most.pipeline.PipelineAccelerometerClassifier;
import org.most.pipeline.PipelineActivityRecognitionCompare;
import org.most.pipeline.PipelineAppOnScreen;
import org.most.pipeline.PipelineAppsNetTraffic;
import org.most.pipeline.PipelineBattery;
import org.most.pipeline.PipelineBluetooth;
import org.most.pipeline.PipelineCell;
import org.most.pipeline.PipelineConnectionType;
import org.most.pipeline.PipelineDR;
import org.most.pipeline.PipelineDeviceNetTraffic;
import org.most.pipeline.PipelineGoogleActivityRecognition;
import org.most.pipeline.PipelineGyroscope;
import org.most.pipeline.PipelineInstalledApps;
import org.most.pipeline.PipelineLight;
import org.most.pipeline.PipelineLocation;
import org.most.pipeline.PipelineMagneticField;
import org.most.pipeline.PipelinePhoneCallDuration;
import org.most.pipeline.PipelinePhoneCallEvent;
import org.most.pipeline.PipelineSystemStats;
import org.most.pipeline.PipelineWifiScan;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.bergmannsoft.util.Utils;
import br.udesc.esag.participactbrasil.domain.persistence.DataQuestionnaireFlat;
import br.udesc.esag.participactbrasil.domain.proto.DataAccelerometerClassifierProtos.DataAccelerometerClassifierProto;
import br.udesc.esag.participactbrasil.domain.proto.DataAccelerometerClassifierProtos.DataAccelerometerClassifierProtoList;
import br.udesc.esag.participactbrasil.domain.proto.DataAccelerometerProtos.DataAccelerometerProto;
import br.udesc.esag.participactbrasil.domain.proto.DataAccelerometerProtos.DataAccelerometerProtoList;
import br.udesc.esag.participactbrasil.domain.proto.DataActivityRecognitionCompareProtos.DataActivityRecognitionCompareProto;
import br.udesc.esag.participactbrasil.domain.proto.DataActivityRecognitionCompareProtos.DataActivityRecognitionCompareProtoList;
import br.udesc.esag.participactbrasil.domain.proto.DataAppOnScreenProtos.DataAppOnScreenProto;
import br.udesc.esag.participactbrasil.domain.proto.DataAppOnScreenProtos.DataAppOnScreenProtoList;
import br.udesc.esag.participactbrasil.domain.proto.DataBatteryProtos.DataBatteryProto;
import br.udesc.esag.participactbrasil.domain.proto.DataBatteryProtos.DataBatteryProtoList;
import br.udesc.esag.participactbrasil.domain.proto.DataBluetoothProtos.DataBluetoothProto;
import br.udesc.esag.participactbrasil.domain.proto.DataBluetoothProtos.DataBluetoothProtoList;
import br.udesc.esag.participactbrasil.domain.proto.DataCellProtos.DataCellProto;
import br.udesc.esag.participactbrasil.domain.proto.DataCellProtos.DataCellProtoList;
import br.udesc.esag.participactbrasil.domain.proto.DataConnectionTypeProtos.DataConnectionTypeProto;
import br.udesc.esag.participactbrasil.domain.proto.DataConnectionTypeProtos.DataConnectionTypeProtoList;
import br.udesc.esag.participactbrasil.domain.proto.DataDRProtos;
import br.udesc.esag.participactbrasil.domain.proto.DataGoogleActivityRecognitionProtos.DataGoogleActivityRecognitionProto;
import br.udesc.esag.participactbrasil.domain.proto.DataGoogleActivityRecognitionProtos.DataGoogleActivityRecognitionProtoList;
import br.udesc.esag.participactbrasil.domain.proto.DataGyroscopeProtos.DataGyroscopeProto;
import br.udesc.esag.participactbrasil.domain.proto.DataGyroscopeProtos.DataGyroscopeProtoList;
import br.udesc.esag.participactbrasil.domain.proto.DataInstalledAppsProtos.DataInstalledAppsProto;
import br.udesc.esag.participactbrasil.domain.proto.DataInstalledAppsProtos.DataInstalledAppsProtoList;
import br.udesc.esag.participactbrasil.domain.proto.DataLightProtos.DataLightProto;
import br.udesc.esag.participactbrasil.domain.proto.DataLightProtos.DataLightProtoList;
import br.udesc.esag.participactbrasil.domain.proto.DataLocationProtos.DataLocationProto;
import br.udesc.esag.participactbrasil.domain.proto.DataLocationProtos.DataLocationProtoList;
import br.udesc.esag.participactbrasil.domain.proto.DataMagneticFieldProtos.DataMagneticFieldProto;
import br.udesc.esag.participactbrasil.domain.proto.DataMagneticFieldProtos.DataMagneticFieldProtoList;
import br.udesc.esag.participactbrasil.domain.proto.DataNetTrafficProtos.DataNetTrafficProto;
import br.udesc.esag.participactbrasil.domain.proto.DataNetTrafficProtos.DataNetTrafficProtoList;
import br.udesc.esag.participactbrasil.domain.proto.DataPhoneCallDurationProtos.DataPhoneCallDurationProto;
import br.udesc.esag.participactbrasil.domain.proto.DataPhoneCallDurationProtos.DataPhoneCallDurationProtoList;
import br.udesc.esag.participactbrasil.domain.proto.DataPhoneCallEventProtos.DataPhoneCallEventProto;
import br.udesc.esag.participactbrasil.domain.proto.DataPhoneCallEventProtos.DataPhoneCallEventProtoList;
import br.udesc.esag.participactbrasil.domain.proto.DataQuestionnaireFlatProtos.DataQuestionnaireFlatProto;
import br.udesc.esag.participactbrasil.domain.proto.DataQuestionnaireFlatProtos.DataQuestionnaireFlatProtoList;
import br.udesc.esag.participactbrasil.domain.proto.DataSystemStatsProtos.DataSystemStatsProto;
import br.udesc.esag.participactbrasil.domain.proto.DataSystemStatsProtos.DataSystemStatsProtoList;
import br.udesc.esag.participactbrasil.domain.proto.DataWifiScanProtos.DataWifiScanProto;
import br.udesc.esag.participactbrasil.domain.proto.DataWifiScanProtos.DataWifiScanProtoList;

public class ContentValuesToProto {

    private static final String FLD_ID = "_ID";

    public static List<Long> getIds(List<ContentValues> values) {
        List<Long> ids = new ArrayList<Long>(values.size());
        for (ContentValues value : values) {
            ids.add(value.getAsLong(FLD_ID));
        }
        return ids;
    }

    public static DataAccelerometerClassifierProtoList convertToDataAccelerometerClassifierProtoList(List<ContentValues> values) {

        List<DataAccelerometerClassifierProto> dataList = new ArrayList<DataAccelerometerClassifierProto>(values.size());

        for (ContentValues value : values) {
            dataList.add(DataAccelerometerClassifierProto.newBuilder()
                    .setSampleTime(value.getAsLong(PipelineAccelerometerClassifier.FLD_TIMESTAMP))
                    .setValue(value.getAsString(PipelineAccelerometerClassifier.FLD_VALUE))
                    .build());
        }
        return DataAccelerometerClassifierProtoList.newBuilder().addAllList(dataList).build();
    }

    public static DataAccelerometerProtoList convertToDataAccelerometerProtoList(List<ContentValues> values) {

        List<DataAccelerometerProto> dataList = new ArrayList<DataAccelerometerProto>(values.size());

        for (ContentValues value : values) {
            // Bergmann
            dataList.add(DataAccelerometerProto.newBuilder()
                    .setSampleTime(value.getAsLong(PipelineAccelerometer.FLD_TIMESTAMP))
                    .setX(value.getAsFloat(PipelineAccelerometer.FLD_X))
                    .setY(value.getAsFloat(PipelineAccelerometer.FLD_Y))
                    .setZ(value.getAsFloat(PipelineAccelerometer.FLD_Z))
                    .build());
        }
        return DataAccelerometerProtoList.newBuilder().addAllList(dataList).build();
    }

    public static DataAppOnScreenProtoList convertToDataAppOnScreenProto(List<ContentValues> values) {

        List<DataAppOnScreenProto> dataList = new ArrayList<DataAppOnScreenProto>(values.size());

        for (ContentValues value : values) {
            dataList.add(DataAppOnScreenProto.newBuilder()
                    .setSampleTime(value.getAsLong(PipelineAppOnScreen.FLD_STARTTIME))
                    .setAppName(value.getAsString(PipelineAppOnScreen.FLD_APPNAME))
                    .setStartTime(value.getAsLong(PipelineAppOnScreen.FLD_STARTTIME))
                    .setEndTime(value.getAsLong(PipelineAppOnScreen.FLD_ENDTIME))
                    .build());
        }
        return DataAppOnScreenProtoList.newBuilder().addAllList(dataList).build();
    }

    public static DataBatteryProtoList convertToDataBatteryProtoList(List<ContentValues> values) {

        List<DataBatteryProto> dataList = new ArrayList<DataBatteryProto>(values.size());

        for (ContentValues value : values) {
            dataList.add(DataBatteryProto.newBuilder()
                    .setSampleTime(value.getAsLong(PipelineBattery.FLD_TIMESTAMP))
                    .setHealth(value.getAsInteger(PipelineBattery.FLD_BATTERY_HEALTH))
                    .setLevel(value.getAsInteger(PipelineBattery.FLD_BATTERY_LEVEL))
                    .setPlugged(value.getAsInteger(PipelineBattery.FLD_BATTERY_PLUGGED))
                    .setScale(value.getAsInteger(PipelineBattery.FLD_BATTERY_SCALE))
                    .setStatus(value.getAsInteger(PipelineBattery.FLD_BATTERY_STATUS))
                    .setTemperature(value.getAsInteger(PipelineBattery.FLD_BATTERY_TEMPERATURE))
                    .setVoltage(value.getAsInteger(PipelineBattery.FLD_BATTERY_VOLTAGE))
                    .build());
        }
        return DataBatteryProtoList.newBuilder().addAllList(dataList).build();
    }

    public static DataBluetoothProtoList convertToDataBluetoothProto(List<ContentValues> values) {

        List<DataBluetoothProto> dataList = new ArrayList<DataBluetoothProto>(values.size());

        for (ContentValues value : values) {
            dataList.add(DataBluetoothProto.newBuilder()
                    .setSampleTime(value.getAsLong(PipelineBluetooth.FLD_TIMESTAMP))
                    .setDeviceClass(value.getAsInteger(PipelineBluetooth.FLD_DEVICECLASS))
                    .setMajorClass(value.getAsInteger(PipelineBluetooth.FLD_DEVICEMAJORCLASS))
                    .setMac(value.getAsString(PipelineBluetooth.FLD_MAC))
                    .setFriendlyName(value.getAsString(PipelineBluetooth.FLD_NAME))
                    .build());
        }
        return DataBluetoothProtoList.newBuilder().addAllList(dataList).build();
    }

    public static DataCellProtoList convertToDataCellProto(List<ContentValues> values) {

        List<DataCellProto> dataList = new ArrayList<DataCellProto>(values.size());

        for (ContentValues value : values) {
            dataList.add(DataCellProto.newBuilder()
                    .setSampleTime(value.getAsLong(PipelineCell.FLD_TIMESTAMP))
                    .setPhoneType(value.getAsString(PipelineCell.FLD_PHONE_TYPE))
                    .setGsmCellId(value.getAsInteger(PipelineCell.FLD_GSM_CELL_ID))
                    .setGsmLac(value.getAsInteger(PipelineCell.FLD_GSM_LAC))
                    .setBaseStationId(value.getAsInteger(PipelineCell.FLD_BASE_STATION_ID))
                    .setBaseStationLatitude(value.getAsInteger(PipelineCell.FLD_BASE_STATION_LATITUDE))
                    .setBaseStationLongitude(value.getAsInteger(PipelineCell.FLD_BASE_STATION_LONGITUDE))
                    .setBaseNetworkId(value.getAsInteger(PipelineCell.FLD_BASE_NETWORK_ID))
                    .setBaseSystemId(value.getAsInteger(PipelineCell.FLD_BASE_SYSTEM_ID))
                    .build());
        }
        return DataCellProtoList.newBuilder().addAllList(dataList).build();
    }

    public static DataGyroscopeProtoList convertToDataGyroscopeProto(List<ContentValues> values) {

        List<DataGyroscopeProto> dataList = new ArrayList<DataGyroscopeProto>(values.size());

        for (ContentValues value : values) {
            dataList.add(DataGyroscopeProto.newBuilder()
                    .setSampleTime(value.getAsLong(PipelineGyroscope.FLD_TIMESTAMP))
                    .setRotationX(value.getAsFloat(PipelineGyroscope.FLD_ROTATION_X))
                    .setRotationY(value.getAsFloat(PipelineGyroscope.FLD_ROTATION_Y))
                    .setRotationZ(value.getAsFloat(PipelineGyroscope.FLD_ROTATION_Z))
                    .build());
        }
        return DataGyroscopeProtoList.newBuilder().addAllList(dataList).build();
    }

    public static DataInstalledAppsProtoList convertToDataInstalledAppsProto(List<ContentValues> values) {

        List<DataInstalledAppsProto> dataList = new ArrayList<DataInstalledAppsProto>(values.size());

        for (ContentValues value : values) {
            dataList.add(DataInstalledAppsProto.newBuilder()
                    .setSampleTime(value.getAsLong(PipelineInstalledApps.FLD_TIMESTAMP))
                    .setPkgName(value.getAsString(PipelineInstalledApps.FLD_PACKAGE_NAME))
                    .setRequestedPermissions(value.getAsString(PipelineInstalledApps.FLD_REQ_PERMISSIONS))
                    .setVersionCode(value.getAsFloat(PipelineInstalledApps.FLD_VERSION_CODE))
                    .setVersionName(value.getAsString(PipelineInstalledApps.FLD_VERSION_NAME))
                    .build());
        }
        return DataInstalledAppsProtoList.newBuilder().addAllList(dataList).build();
    }

    public static DataLightProtoList convertToDataLightProto(List<ContentValues> values) {

        List<DataLightProto> dataList = new ArrayList<DataLightProto>(values.size());

        for (ContentValues value : values) {
            dataList.add(DataLightProto.newBuilder()
                    .setSampleTime(value.getAsLong(PipelineLight.FLD_TIMESTAMP))
                    .setValue(value.getAsFloat(PipelineLight.FLD_VALUE))
                    .build());
        }
        return DataLightProtoList.newBuilder().addAllList(dataList).build();
    }

    public static DataLocationProtoList convertToDataLocationProtoList(List<ContentValues> values) {

        List<DataLocationProto> dataList = new ArrayList<DataLocationProto>(values.size());

        for (ContentValues value : values) {
            dataList.add(DataLocationProto.newBuilder()
                    .setSampleTime(value.getAsLong(PipelineLocation.FLD_TIMESTAMP))
                    .setAccuracy(value.getAsDouble(PipelineLocation.FLD_ACCURACY))
                    .setLatitude(value.getAsDouble(PipelineLocation.FLD_LATITUDE))
                    .setLongitude(value.getAsDouble(PipelineLocation.FLD_LONGITUDE))
                    .setProvider(value.getAsString(PipelineLocation.FLD_PROVIDER))
                    .build());
        }
        return DataLocationProtoList.newBuilder().addAllList(dataList).build();
    }

    public static DataMagneticFieldProtoList convertToDataMagneticFieldProto(List<ContentValues> values) {

        List<DataMagneticFieldProto> dataList = new ArrayList<DataMagneticFieldProto>(values.size());

        for (ContentValues value : values) {
            dataList.add(DataMagneticFieldProto.newBuilder()
                    .setSampleTime(value.getAsLong(PipelineMagneticField.FLD_TIMESTAMP))
                    .setMagneticFieldX(value.getAsFloat(PipelineMagneticField.FLD_MAGNETIC_FIELD_X))
                    .setMagneticFieldY(value.getAsFloat(PipelineMagneticField.FLD_MAGNETIC_FIELD_Y))
                    .setMagneticFieldZ(value.getAsFloat(PipelineMagneticField.FLD_MAGNETIC_FIELD_Z))
                    .build());
        }
        return DataMagneticFieldProtoList.newBuilder().addAllList(dataList).build();
    }

    public static DataPhoneCallDurationProtoList convertToDataPhoneCallDurationProto(List<ContentValues> values) {

        List<DataPhoneCallDurationProto> dataList = new ArrayList<DataPhoneCallDurationProto>(values.size());

        for (ContentValues value : values) {
            dataList.add(DataPhoneCallDurationProto.newBuilder()
                    .setSampleTime(value.getAsLong(PipelinePhoneCallDuration.FLD_TIMESTAMP))
                    .setCallEnd(value.getAsLong(PipelinePhoneCallDuration.FLD_CALL_END))
                    .setCallStart(value.getAsLong(PipelinePhoneCallDuration.FLD_CALL_START))
                    .setIsIncoming(value.getAsBoolean(PipelinePhoneCallDuration.FLD_IS_INCOMING))
                    .setPhoneNumber(value.getAsString(PipelinePhoneCallDuration.FLD_PHONE_NUMBER))
                    .build());
        }
        return DataPhoneCallDurationProtoList.newBuilder().addAllList(dataList).build();
    }

    public static DataPhoneCallEventProtoList convertToDataPhoneCallEventProto(List<ContentValues> values) {

        List<DataPhoneCallEventProto> dataList = new ArrayList<DataPhoneCallEventProto>(values.size());

        for (ContentValues value : values) {
            dataList.add(DataPhoneCallEventProto.newBuilder()
                    .setSampleTime(value.getAsLong(PipelinePhoneCallEvent.FLD_TIMESTAMP))
                    .setIsIncoming(value.getAsBoolean(PipelinePhoneCallEvent.FLD_IS_INCOMING))
                    .setIsStart(value.getAsBoolean(PipelinePhoneCallEvent.FLD_IS_START))
                    .setPhoneNumber(value.getAsString(PipelinePhoneCallEvent.FLD_PHONE_NUMBER))
                    .build());
        }
        return DataPhoneCallEventProtoList.newBuilder().addAllList(dataList).build();
    }

    public static DataSystemStatsProtoList convertToDataSystemStatsProto(List<ContentValues> values) {

        List<DataSystemStatsProto> dataList = new ArrayList<DataSystemStatsProto>(values.size());

        for (ContentValues value : values) {
            dataList.add(DataSystemStatsProto.newBuilder()
                    .setSampleTime(value.getAsLong(PipelineSystemStats.FLD_TIMESTAMP))
                    .setBOOTTIME(value.getAsLong(PipelineSystemStats.FLD_BOOT_TIME))
                    .setCONTEXTSWITCHES(value.getAsLong(PipelineSystemStats.FLD_CONTEXT_SWITCH))
                    .setCPUFREQUENCY(value.getAsFloat(PipelineSystemStats.FLD_CPU_FREQ))
                    .setCPUHARDIRQ(value.getAsLong(PipelineSystemStats.FLD_CPU_HARDIRQ))
                    .setCPUIDLE(value.getAsLong(PipelineSystemStats.FLD_CPU_IDLE))
                    .setCPUIOWAIT(value.getAsLong(PipelineSystemStats.FLD_CPU_IOWAIT))
                    .setCPUNICED(value.getAsLong(PipelineSystemStats.FLD_CPU_NICE))
                    .setCPUSOFTIRQ(value.getAsLong(PipelineSystemStats.FLD_CPU_SOFTIRQ))
                    .setCPUSYSTEM(value.getAsLong(PipelineSystemStats.FLD_CPU_SYSTEM))
                    .setCPUUSER(value.getAsLong(PipelineSystemStats.FLD_CPU_USER))
                    .setMEMACTIVE(value.getAsLong(PipelineSystemStats.FLD_MEM_ACTIVE))
                    .setMEMFREE(value.getAsLong(PipelineSystemStats.FLD_MEM_FREE))
                    .setMEMINACTIVE(value.getAsLong(PipelineSystemStats.FLD_MEM_INACTIVE))
                    .setMEMTOTAL(value.getAsLong(PipelineSystemStats.FLD_MEM_TOTAL))
                    .setPROCESSES(value.getAsLong(PipelineSystemStats.FLD_PROCESSES))
                    .build());
        }
        return DataSystemStatsProtoList.newBuilder().addAllList(dataList).build();
    }

    public static DataWifiScanProtoList convertToDataWifiScanProto(List<ContentValues> values) {

        List<DataWifiScanProto> dataList = new ArrayList<DataWifiScanProto>(values.size());

        for (ContentValues value : values) {
            dataList.add(DataWifiScanProto.newBuilder()
                    .setSampleTime(value.getAsLong(PipelineWifiScan.FLD_TIMESTAMP))
                    .setBssid(value.getAsString(PipelineWifiScan.FLD_BSSID))
                    .setSsid(value.getAsString(PipelineWifiScan.FLD_SSID))
                    .setCapabilities(value.getAsString(PipelineWifiScan.FLD_CAPABILITIES))
                    .setFrequency(value.getAsInteger(PipelineWifiScan.FLD_FREQUENCY))
                    .setLevel(value.getAsInteger(PipelineWifiScan.FLD_LEVEL))
                    .build());
        }
        return DataWifiScanProtoList.newBuilder().addAllList(dataList).build();
    }


    public static DataQuestionnaireFlatProtoList convertToDataQuestionnaireFlatProto(List<DataQuestionnaireFlat> values) {

        List<DataQuestionnaireFlatProto> dataList = new ArrayList<DataQuestionnaireFlatProto>(values.size());

        for (DataQuestionnaireFlat value : values) {
            dataList.add(DataQuestionnaireFlatProto.newBuilder()
                    .setSampleTime(value.getTimestamp())
                    .setTaskId(value.getTaskId())
                    .setActionId(value.getActionId())
                    .setQuestionId(value.getQuestionId())
                    .setType(value.getType())
                    .setAnswerId(value.getAnswerId())
                    .setClosedAnswerValue(value.getClosedAnswerValue())
                    .setOpenAnswerValue(value.getOpenAnswerValue())
                    .build());
        }
        return DataQuestionnaireFlatProtoList.newBuilder().addAllList(dataList).build();
    }

    public static DataNetTrafficProtoList convertToDataDeviceNetTrafficProto(List<ContentValues> values) {

        List<DataNetTrafficProto> dataList = new ArrayList<DataNetTrafficProto>(values.size());

        for (ContentValues value : values) {
            dataList.add(DataNetTrafficProto.newBuilder()
                    .setSampleTime(value.getAsLong(PipelineDeviceNetTraffic.FLD_TIMESTAMP))
                    .setAppName("")
                    .setTxBytes(value.getAsLong(PipelineDeviceNetTraffic.FLD_TX_BYTES))
                    .setRxBytes(value.getAsLong(PipelineDeviceNetTraffic.FLD_RX_BYTES))
                    .build());
        }
        return DataNetTrafficProtoList.newBuilder().addAllList(dataList).build();
    }

    public static DataNetTrafficProtoList convertToDataAppsNetTrafficProto(List<ContentValues> values) {

        List<DataNetTrafficProto> dataList = new ArrayList<DataNetTrafficProto>(values.size());

        for (ContentValues value : values) {
            dataList.add(DataNetTrafficProto.newBuilder()
                    .setSampleTime(value.getAsLong(PipelineAppsNetTraffic.FLD_TIMESTAMP))
                    .setAppName(value.getAsString(PipelineAppsNetTraffic.FLD_APP_NAME))
                    .setTxBytes(value.getAsLong(PipelineAppsNetTraffic.FLD_TX_BYTES))
                    .setRxBytes(value.getAsLong(PipelineAppsNetTraffic.FLD_RX_BYTES))
                    .build());
        }
        return DataNetTrafficProtoList.newBuilder().addAllList(dataList).build();
    }

    public static DataConnectionTypeProtoList convertToDataConnectionTypeProto(List<ContentValues> values) {

        List<DataConnectionTypeProto> dataList = new ArrayList<DataConnectionTypeProto>(values.size());

        for (ContentValues value : values) {
            dataList.add(DataConnectionTypeProto.newBuilder()
                    .setSampleTime(value.getAsLong(PipelineConnectionType.FLD_TIMESTAMP))
                    .setConnectionType(value.getAsString(PipelineConnectionType.FLD_TYPE))
                    .setMobileNetworkType(value.getAsString(PipelineConnectionType.FLD_MOBILE_NETWORK_TYPE))
                    .build());
        }
        return DataConnectionTypeProtoList.newBuilder().addAllList(dataList).build();
    }

    public static DataDRProtos.DataDRProtoList convertToDataDRProtoList(List<ContentValues> values) {

        List<DataDRProtos.DataDRProto> dataList = new ArrayList<DataDRProtos.DataDRProto>(values.size());

        for (ContentValues value : values) {
            dataList.add(DataDRProtos.DataDRProto.newBuilder()
                    .setState(value.getAsString(PipelineDR.FLD_DR_STATE))
                    .setPole(value.getAsString(PipelineDR.FLD_DR_POLE))
                    .setLatitude(value.getAsFloat(PipelineDR.FLD_DR_LATITUDE))
                    .setLongitude(value.getAsFloat(PipelineDR.FLD_DR_LONGITUDE))
                    .setAccuracy(value.getAsFloat(PipelineDR.FLD_DR_ACCURACY))
                    .setTimestamp(value.getAsLong(PipelineDR.FLD_DR_TIMESTAMP))
                    .setStatus(value.getAsString(PipelineDR.FLD_DR_STATUS))
                    .build());
        }

        return DataDRProtos.DataDRProtoList.newBuilder().addAllList(dataList).build();
    }

    public static DataActivityRecognitionCompareProtoList convertToDataActivityRecognitionComparatorProtoList(List<ContentValues> values) {

        List<DataActivityRecognitionCompareProto> dataList = new ArrayList<DataActivityRecognitionCompareProto>(values.size());

        for (ContentValues value : values) {
            dataList.add(DataActivityRecognitionCompareProto.newBuilder()
                    .setSampleTime(value.getAsLong(PipelineActivityRecognitionCompare.FLD_TIMESTAMP))
                    .setUserActivity(value.getAsString(PipelineActivityRecognitionCompare.FLD_USER_ACTIVITY))
                    .setGoogleArTimestamp(value.getAsLong(PipelineActivityRecognitionCompare.FLD_G_TIMESTAMP))
                    .setGoogleArValue(value.getAsString(PipelineActivityRecognitionCompare.FLD_G_RECOGNIZED_ACTIVITY))
                    .setGoogleArConfidence(value.getAsInteger(PipelineActivityRecognitionCompare.FLD_G_CONFIDENCE))
                    .setMostArTimestamp(value.getAsLong(PipelineActivityRecognitionCompare.FLD_D_TIMESTAMP))
                    .setMostArValue(value.getAsString(PipelineActivityRecognitionCompare.FLD_D_VALUE))
                    .build());
        }
        return DataActivityRecognitionCompareProtoList.newBuilder().addAllList(dataList).build();
    }

    public static DataGoogleActivityRecognitionProtoList convertToDataGoogleActivityRecognitionProtoList(List<ContentValues> values) {

        List<DataGoogleActivityRecognitionProto> dataList = new ArrayList<DataGoogleActivityRecognitionProto>(values.size());

        for (ContentValues value : values) {
            dataList.add(DataGoogleActivityRecognitionProto.newBuilder()
                    .setSampleTime(value.getAsLong(PipelineGoogleActivityRecognition.FLD_TIMESTAMP))
                    .setRecognizedActivity(value.getAsString(PipelineGoogleActivityRecognition.FLD_RECOGNIZED_ACTIVITY))
                    .setConfidence(value.getAsInteger(PipelineGoogleActivityRecognition.FLD_CONFIDENCE))
                    .build());
        }
        return DataGoogleActivityRecognitionProtoList.newBuilder().addAllList(dataList).build();
    }


}
