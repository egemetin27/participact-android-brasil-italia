package br.udesc.esag.participactbrasil.domain.persistence.support;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

import br.udesc.esag.participactbrasil.R;
import br.udesc.esag.participactbrasil.domain.persistence.ActionFlat;
import br.udesc.esag.participactbrasil.domain.persistence.ClosedAnswer;
import br.udesc.esag.participactbrasil.domain.persistence.DataQuestionnaireFlat;
import br.udesc.esag.participactbrasil.domain.persistence.Question;
import br.udesc.esag.participactbrasil.domain.persistence.QuestionnaireProgressPerAction;
import br.udesc.esag.participactbrasil.domain.persistence.RemainingPhotoPerAction;
import br.udesc.esag.participactbrasil.domain.persistence.TaskFlat;
import br.udesc.esag.participactbrasil.domain.persistence.TaskStatus;

public class DomainDBHelper extends OrmLiteSqliteOpenHelper {

    private static final String DB_NAME = "domain.db";
    private static final int DB_VERSION = 1;


    public DomainDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION, R.raw.ormlite_domain_config);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try {

            TableUtils.createTableIfNotExists(connectionSource, ActionFlat.class);
            TableUtils.createTableIfNotExists(connectionSource, TaskFlat.class);
            TableUtils.createTableIfNotExists(connectionSource, TaskStatus.class);
            TableUtils.createTableIfNotExists(connectionSource, QuestionnaireProgressPerAction.class);
            TableUtils.createTableIfNotExists(connectionSource, RemainingPhotoPerAction.class);
            TableUtils.createTableIfNotExists(connectionSource, ClosedAnswer.class);
            TableUtils.createTableIfNotExists(connectionSource, Question.class);
            TableUtils.createTableIfNotExists(connectionSource, DataQuestionnaireFlat.class);

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int i, int i2) {
        onCreate(sqLiteDatabase, connectionSource);
    }

}
