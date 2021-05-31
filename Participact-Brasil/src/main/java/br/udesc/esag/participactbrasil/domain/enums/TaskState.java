package br.udesc.esag.participactbrasil.domain.enums;

public enum TaskState {
    AVAILABLE, ACCEPTED, RUNNING, REJECTED, FAILED, INTERRUPTED, ERROR, UNKNOWN, SUSPENDED, ANY, COMPLETED_NOT_SYNC_WITH_SERVER, COMPLETED_WITH_SUCCESS, COMPLETED_WITH_UNSUCCESS, RUNNING_BUT_NOT_EXEC, HIDDEN, GEO_NOTIFIED_AVAILABLE;

    //cant override toString because is used on the requests to the server
    public String toReadableString() {
        switch (this) {
            case AVAILABLE:
                return "Disponível";

            case ACCEPTED:
                return "Aceita";

            case RUNNING:
                return "Em andamento";

            case REJECTED:
                return "Rejeitada";

            case FAILED:
                return "Campanha  falhou";

            case INTERRUPTED:
                return "Campanha interrompida";

            case ERROR:
                return "Campanha com erro";

            case ANY:
            case UNKNOWN:
            case HIDDEN:
                return "Status não disponível";

            case SUSPENDED:
                return "Suspensa";

            case COMPLETED_NOT_SYNC_WITH_SERVER:
                return "Concluida, não sincronizada";

            case COMPLETED_WITH_SUCCESS:
                return "Concluida com sucesso";

            case COMPLETED_WITH_UNSUCCESS:
                return "Concluida com falha";

            case RUNNING_BUT_NOT_EXEC:
                return "Em andamento, sem execução";

            case GEO_NOTIFIED_AVAILABLE:
                return "Disponivel por localização";

            default:
                return "Indefinido";
        }
    }
}
