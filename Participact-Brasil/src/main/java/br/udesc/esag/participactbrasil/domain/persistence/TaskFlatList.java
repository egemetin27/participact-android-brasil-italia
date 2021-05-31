package br.udesc.esag.participactbrasil.domain.persistence;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;



public class TaskFlatList implements Serializable {

    private static final long serialVersionUID = 2215478037991502928L;

    private List<TaskFlat> _list;

    public TaskFlatList() {
        _list = new ArrayList<TaskFlat>();
    }

    public TaskFlatList(List<TaskFlat> list) {
        _list = list;
    }

    public List<TaskFlat> getList() {
        return _list;
    }

    public void setList(List<TaskFlat> _list) {
        this._list = _list;
    }

}