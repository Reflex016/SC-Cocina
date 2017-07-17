package pae.app.dbconnections;

import java.util.HashMap;
import java.util.List;

import pae.dbconnections.IDbConnection;

public interface IDataAdapter<T> {

    public T getRecord(IDbConnection db, HashMap<String,Object> options);

    public List<T> getList(IDbConnection db, HashMap<String,Object> options);

    public boolean insertRecord(IDbConnection db, T record, HashMap<String,Object> options);

    public boolean updateRecord(IDbConnection db, T record, HashMap<String,Object> options);

    public boolean deleteRecord(IDbConnection db, T record, HashMap<String,Object> options);

}
