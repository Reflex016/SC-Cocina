package pae.alimentos.dbconnections;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import pae.dbconnections.DbException;
import pae.dbconnections.IDbConnection;
import pae.dbconnections.PostgresDbConnection;
import pae.alimentos.models.Alimento;

public class AlimentoDbAdapter implements IDataAdapter<Alimento>{
    //flag to drive open/close connection in local methods
    boolean localOpen;
    
    @Override
    public List<Alimento> getList(IDbConnection db, 
            HashMap<String, Object> options) {
        List<Alimento> list = new ArrayList<>();
        PostgresDbConnection postgresDb = (PostgresDbConnection) db;
        
        try { 
            if (!postgresDb.isOpen()){
                postgresDb.open(true);
                localOpen = true;
            }    
            
            String sql = "SELECT * FROM public.alimentos WHERE 1=1 ORDER BY alimentos.id;";
            
            if (options != null){
                String order = (String) options.get("order");
                if (order != null)
                    sql += " ORDER BY " + order;
            }
            
            ResultSet rs = postgresDb.getResultSet(sql);
            while (rs.next()){
                Alimento al = new Alimento(rs.getString("nombre"),
                                        rs.getDouble("cantidad"),
                                        rs.getString("descripcion"));
                list.add(al);
            }
        } catch (DbException | SQLException ex) {
            Logger.getLogger(AlimentoDbAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if (localOpen){
            localOpen = false;
            postgresDb.close();
        }
        return list;
    }


    @Override
    public double getCantidad(IDbConnection db,HashMap<String, Object> options,Alimento alimento) {
            double cantidad_disponible=0.0;
            PostgresDbConnection postgresDb = (PostgresDbConnection) db;

            try {
                if (!postgresDb.isOpen()){
                    postgresDb.open(true);
                    localOpen = true;
                }

                String sql = "SELECT cantidad FROM public.alimentos WHERE nombre='"+alimento.getNombre()+"'";
                System.out.println("SELECT SQL:" + sql);

                if (options != null){
                    String order = (String) options.get("order");
                    if (order != null)
                        sql += " ORDER BY " + order;
                }

                ResultSet rs = postgresDb.getResultSet(sql);
                while (rs.next()){
                    cantidad_disponible=rs.getDouble("cantidad");
                }
            } catch (DbException | SQLException ex) {
                Logger.getLogger(AlimentoDbAdapter.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (localOpen){
                localOpen = false;
                postgresDb.close();
            }



        return cantidad_disponible;
    }

    @Override
    public Alimento getRecord(IDbConnection db, HashMap<String, Object> options) {
        List<Alimento> list = getList(db, options);
        if (list.size()>0) return list.get(0);
        return null;
    }
    
    @Override
    public boolean insertRecord(IDbConnection db, Alimento record, HashMap<String, Object> options) {
        PostgresDbConnection postgresDb = (PostgresDbConnection) db;
        int insertedRecords = 0;
        
        try { 
            if (!postgresDb.isOpen()){
                postgresDb.open(false);
                localOpen = true;
            }
            
            String sql = "INSERT INTO public.alimentos(nombre, cantidad, descripcion) "
                    + "VALUES ('" + record.getNombre() + "', "
                    + record.getCantidad() + ", '" + record.getDescripcion() + "')";
            System.out.println("INSERT SQL:" + sql);
            
            insertedRecords = postgresDb.executeUpdate(sql);

        } catch (DbException | SQLException ex) {
            Logger.getLogger(AlimentoDbAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if (localOpen){
            localOpen = false;
            postgresDb.close();
        }
        
        return (insertedRecords>0);
    }

    @Override
    public boolean updateRecord(IDbConnection db, Alimento record, HashMap<String, Object> options) {
        PostgresDbConnection postgresDb = (PostgresDbConnection) db;
        int updatedRecords = 0;
        
        try { 
            if (!postgresDb.isOpen()){
                postgresDb.open(false);
                localOpen = true;
            }    
            
            String sql = "UPDATE public.alimentos "
                    + "SET cantidad = " + record.getCantidad()
                    + "WHERE nombre = '" + record.getNombre();
               
            System.out.println("UPDATE SQL:" + sql);

            updatedRecords = postgresDb.executeUpdate(sql);
        } catch (DbException | SQLException ex) {
            Logger.getLogger(AlimentoDbAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if (localOpen){
            localOpen = false;
            postgresDb.close();
        }
        
        return (updatedRecords>0);
    }



    @Override
    public boolean updateInsumo(IDbConnection db, Alimento record, HashMap<String, Object> options, double cantidad_disponible) {

        PostgresDbConnection postgresDb = (PostgresDbConnection) db;
        int updatedRecords = 0;
        double nueva_disponibilidad=cantidad_disponible+record.getCantidad();
        try {
            if (!postgresDb.isOpen()){
                postgresDb.open(false);
                localOpen = true;
            }
            String sql = "UPDATE public.alimentos set cantidad='"+nueva_disponibilidad+"' WHERE nombre='"+record.getNombre()+"'";
            System.out.println("UPDATE SQL:" + sql);
            updatedRecords = postgresDb.executeUpdate(sql);

        } catch (DbException | SQLException ex) {
            Logger.getLogger(AlimentoDbAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (localOpen){
            localOpen = false;
            postgresDb.close();
        }

        return (updatedRecords>0);


    }


    @Override
    public boolean deleteRecord(IDbConnection db, Alimento record, HashMap<String, Object> options) {
        PostgresDbConnection postgresDb = (PostgresDbConnection) db;
        int deletedRecords = 0;
        
        try { 
            if (!postgresDb.isOpen()){
                postgresDb.open(false);
                localOpen = true;
            }    
            
            String sql = "DELETE FROM public.alimentos "
                    + "WHERE nombre = '" + record.getNombre() + "'";
            
            System.out.println("DELETE SQL:" + sql);
            
            deletedRecords = postgresDb.executeUpdate(sql);
        } catch (DbException | SQLException ex) {
            Logger.getLogger(AlimentoDbAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if (localOpen){
            localOpen = false;
            postgresDb.close();
        }
        
        return (deletedRecords > 0);        
    }

    public List<Alimento> getInsumoName(IDbConnection db,
                                        HashMap<String, Object> options, String nombre) {
        List<Alimento> list = new ArrayList<>();
        PostgresDbConnection postgresDb = (PostgresDbConnection) db;

        try {
            if (!postgresDb.isOpen()){
                postgresDb.open(true);
                localOpen = true;
            }

            String sql = "SELECT * FROM public.alimentos WHERE alimentos.nombre = '"+nombre+"';";

            if (options != null){
                String order = (String) options.get("order");
                if (order != null)
                    sql += " ORDER BY " + order;
            }

            ResultSet rs = postgresDb.getResultSet(sql);
            while (rs.next()){
                Alimento user = new Alimento(rs.getString("nombre"),
                        rs.getDouble("cantidad"),
                        rs.getString("descripcion"));
                list.add(user);
            }
        } catch (DbException | SQLException ex) {
            Logger.getLogger(pae.app.dbconnections.UsuarioDbAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (localOpen){
            localOpen = false;
            postgresDb.close();
        }
        return list;
    }

    public int getIdInsumo(IDbConnection db,
                                        HashMap<String, Object> options, String nombre) {
        int id = 0;
        PostgresDbConnection postgresDb = (PostgresDbConnection) db;

        try {
            if (!postgresDb.isOpen()){
                postgresDb.open(true);
                localOpen = true;
            }

            String sql = "SELECT * FROM public.alimentos WHERE alimentos.nombre = '"+nombre+"';";

            if (options != null){
                String order = (String) options.get("order");
                if (order != null)
                    sql += " ORDER BY " + order;
            }

            ResultSet rs = postgresDb.getResultSet(sql);
            while (rs.next()){
                Alimento alimento = new Alimento(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getDouble("cantidad"),
                        rs.getString("descripcion"));
                id = alimento.getId();
            }
        } catch (DbException | SQLException ex) {
            Logger.getLogger(pae.app.dbconnections.UsuarioDbAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (localOpen){
            localOpen = false;
            postgresDb.close();
        }
        return id;
    }
    
}