/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pae.alimentos.dbconnections;

import java.util.HashMap;
import java.util.List;


import pae.dbconnections.IDbConnection;

public interface IDataAdapter<T> {
    
    public T getRecord(IDbConnection db, HashMap<String,Object> options);

    public List<T> getList(IDbConnection db, HashMap<String,Object> options);

    public double  getCantidad(IDbConnection db, HashMap<String,Object> options,T record);

    public boolean insertRecord(IDbConnection db, T record, HashMap<String,Object> options);
    
    public boolean updateRecord(IDbConnection db, T record, HashMap<String,Object> options);

    public boolean updateInsumo(IDbConnection db, T record, HashMap<String,Object> options, double cantidad_disponible);
    
    public boolean deleteRecord(IDbConnection db, T record, HashMap<String,Object> options);
}
