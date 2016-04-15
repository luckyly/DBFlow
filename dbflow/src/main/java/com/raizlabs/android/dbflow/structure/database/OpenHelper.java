package com.raizlabs.android.dbflow.structure.database;

/**
 * Description: Abstracts out the {@link DatabaseHelperDelegate} into the one used in this library.
 */
public interface OpenHelper {

    DatabaseWrapper getDatabase();

    DatabaseHelperDelegate getDelegate();

    boolean isDatabaseIntegrityOk();

    void backupDB();

    void setDatabaseListener(DatabaseHelperListener helperListener);
}
