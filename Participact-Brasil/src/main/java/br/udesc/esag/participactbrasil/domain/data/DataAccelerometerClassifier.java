package br.udesc.esag.participactbrasil.domain.data;


public class DataAccelerometerClassifier extends Data {

    private static final long serialVersionUID = 7324068068645508192L;

    private String classifier_value;

    public String getValue() {
        return classifier_value;
    }

    public void setValue(String classifier_value) {
        this.classifier_value = classifier_value;
    }

}
