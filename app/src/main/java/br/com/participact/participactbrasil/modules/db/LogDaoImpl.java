package br.com.participact.participactbrasil.modules.db;

import java.util.List;

import br.com.participact.participactbrasil.modules.App;

public class LogDaoImpl {

    private LogDao dao = App.getInstance().getDaoSession().getLogDao();

    public void save(Log log) {
        dao.insert(log);
    }

    public Log pop() {
        List<Log> entities = dao.queryBuilder().limit(1).build().list();
        if (entities.size() > 0) {
            return entities.get(0);
        }
        return null;
    }

    public void delete(Log log) {
        dao.delete(log);
    }
}
