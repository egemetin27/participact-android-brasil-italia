package br.udesc.esag.participactbrasil.domain.rest;

import java.util.List;
import java.util.Objects;

import br.udesc.esag.participactbrasil.domain.persistence.Message;
import br.udesc.esag.participactbrasil.domain.rest.base.RestResult;

/**
 * Created by fabiobergmann on 17/10/16.
 */

public class NotificationsResult extends RestResult {

    List<List<Object>> items;

    public List<List<Object>> getItems() {
        return items;
    }
}
