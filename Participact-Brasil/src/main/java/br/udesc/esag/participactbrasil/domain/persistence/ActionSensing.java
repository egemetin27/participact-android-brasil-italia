package br.udesc.esag.participactbrasil.domain.persistence;


public class ActionSensing extends Action {

    private static final long serialVersionUID = -8422766029359447220L;

    private Integer input_type;

    public Integer getInput_type() {
        return input_type;
    }

    public void setInput_type(Integer input_type) {
        this.input_type = input_type;
    }

    public ActionFlat convertToActionFlat() {
        return new ActionFlat(this);
    }
}
