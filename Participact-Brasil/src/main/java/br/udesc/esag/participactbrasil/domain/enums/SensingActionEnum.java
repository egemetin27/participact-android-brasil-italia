package br.udesc.esag.participactbrasil.domain.enums;

public class SensingActionEnum {

    public static enum Type {
        DUMMY, AUDIO_CLASSIFIER, ACCELEROMETER, ACCELEROMETER_CLASSIFIER, RAW_AUDIO, AVERAGE_ACCELEROMETER, APP_ON_SCREEN, APPS_NET_TRAFFIC, BATTERY, BLUETOOTH, CELL, CONNECTION_TYPE, DEVICE_NET_TRAFFIC, GYROSCOPE, INSTALLED_APPS, LIGHT, LOCATION, MAGNETIC_FIELD, PHONE_CALL_DURATION, PHONE_CALL_EVENT, SYSTEM_STATS, WIFI_SCAN, TEST;

        /**
         * Converts an integer to a valid Pipeline type.
         *
         * @param value The integer to convert
         * @return The Type represented by <code>value</code>. If the conversion
         * fails, returns DUMMY.
         */
        public static Type fromInt(int value) {
            switch (value) {
                case 1:
                    return ACCELEROMETER;
                case 2:
                    return RAW_AUDIO;
                case 3:
                    return AVERAGE_ACCELEROMETER;
                case 4:
                    return APP_ON_SCREEN;
                case 5:
                    return BATTERY;
                case 6:
                    return BLUETOOTH;
                case 7:
                    return CELL;
                case 8:
                    return GYROSCOPE;
                case 9:
                    return INSTALLED_APPS;
                case 10:
                    return LIGHT;
                case 11:
                    return LOCATION;
                case 12:
                    return MAGNETIC_FIELD;
                case 13:
                    return PHONE_CALL_DURATION;
                case 14:
                    return PHONE_CALL_EVENT;
                case 15:
                    return ACCELEROMETER_CLASSIFIER;
                case 16:
                    return SYSTEM_STATS;
                case 17:
                    return WIFI_SCAN;
                case 18:
                    return AUDIO_CLASSIFIER;
                case 19:
                    return DEVICE_NET_TRAFFIC;
                case 20:
                    return APPS_NET_TRAFFIC;
                case 21:
                    return CONNECTION_TYPE;
                case 99:
                    return TEST;
                default:
                    return DUMMY;
            }
        }

        /*ParticipActBrasil - Traducao*/
        public static String fromIntToHumanReadable(int value) {
            switch (value) {
                case 1:
                    return "Acelerometro"; /*"Accelerometro";*/
                case 2:
                    return "Audio"; /*"Audio";*/
                case 3:
                    return "Acelerometro"; /*"Accelerometro";*/
                case 4:
                    return "Aplicativos utilizados"; /*"Applicazione in uso";*/
                case 5:
                    return "Nível de bateria"; /*"Livello batteria";*/
                case 6:
                    return "Bluetooth"; /*"Bluetooth";*/
                case 7:
                    return "Estacao de celular"; /*"Stazione cellulare";*/
                case 8:
                    return "Giroscopio"; /*"Giroscopio";*/
                case 9:
                    return "Aplicativos instalados"; /*"Applicazioni installate";*/
                case 10:
                    return "Sensor de luminosidade"; /*"Sensore di luminosità";*/
                case 11:
                    return "Localizacao geografica"; /*"Posizione geografica";*/
                case 12:
                    return "Magnetometro"; /*"Magnetometro";*/
                case 13:
                    return "Duracao das chamadas"; /*"Durata telefonate";*/
                case 14:
                    return "Numdero do telefone"; /*"Numero di telefonate";*/
                case 15:
                    return "Atividade fisica"; /*"Attività fisica";*/
                case 16:
                    return "Sistema de estatisticas"; /*"Statistiche di sistema";*/
                case 17:
                    return "Wifi"; /*"Wifi";*/
                case 18:
                    return "Tipo de som (Microfone)"; /*"Tipo di suono (Microfono)";*/
                case 19:
                    return "Medicao do volume de dados trafegados do dispositivo"; /*"Misura volume traffico dati dispositivo";*/
                case 20:
                    return "Medicao do volume de dados trafegados do aplicativo"; /*"Misura volume traffico dati applicazioni";*/
                case 21:
                    return "Tipo de conexao"; /*"Tipo di connessione";*/
                case 22:
                    return "Atividade fisica V.2"; /*"Attività fisica V.2";*/
                case 23:
                    return "Atividades de reconhecimento"; /*"Riconoscimento Attività";*/
                case 24:
                    return "Reconhecimento dinamico"; /*"Riconoscimenti Dinamico";*/
                case 99:
                    return "Teste"; /*"Test";*/
                default:
                    return "Imitacao"; /*"Dummy";*/
            }
        }

        /**
         * Converts the Pipeline type to an integer.
         *
         * @return
         */
        public int toInt() {
            switch (this) {
                case ACCELEROMETER:
                    return 1;
                case RAW_AUDIO:
                    return 2;
                case AVERAGE_ACCELEROMETER:
                    return 3;
                case APP_ON_SCREEN:
                    return 4;
                case BATTERY:
                    return 5;
                case BLUETOOTH:
                    return 6;
                case CELL:
                    return 7;
                case GYROSCOPE:
                    return 8;
                case INSTALLED_APPS:
                    return 9;
                case LIGHT:
                    return 10;
                case LOCATION:
                    return 11;
                case MAGNETIC_FIELD:
                    return 12;
                case PHONE_CALL_DURATION:
                    return 13;
                case PHONE_CALL_EVENT:
                    return 14;
                case ACCELEROMETER_CLASSIFIER:
                    return 15;
                case SYSTEM_STATS:
                    return 16;
                case WIFI_SCAN:
                    return 17;
                case AUDIO_CLASSIFIER:
                    return 18;
                case DEVICE_NET_TRAFFIC:
                    return 19;
                case APPS_NET_TRAFFIC:
                    return 20;
                case CONNECTION_TYPE:
                    return 21;
                case TEST:
                    return 99;
                default:
                    return 0;
            }
        }

    }
}
