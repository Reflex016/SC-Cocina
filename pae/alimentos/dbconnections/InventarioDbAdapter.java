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
import pae.alimentos.models.Inventario;

public class InventarioDbAdapter implements IDataAdapter<Inventario>{
    //flag to drive open/close connection in local methods
    boolean localOpen;

    @Override
    public List<Inventario> getList(IDbConnection db,
                                  HashMap<String, Object> options) {
        List<Inventario> list = new ArrayList<>();
        PostgresDbConnection postgresDb = (PostgresDbConnection) db;

        try {
            if (!postgresDb.isOpen()){
                postgresDb.open(true);
                localOpen = true;
            }

            String sql = "SELECT * FROM public.inventarios WHERE 1=1";

            if (options != null){
                String order = (String) options.get("order");
                if (order != null)
                    sql += " ORDER BY " + order;
            }

            ResultSet rs = postgresDb.getResultSet(sql);
            while (rs.next()){
                Inventario inventario = new Inventario(rs.getInt("id_alimento"),
                        rs.getString("accion"),
                        rs.getDouble("cantidad"),
                        rs.getString("fecha"));
                list.add(inventario);
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
    public double getCantidad(IDbConnection db, HashMap<String, Object> options, Inventario record) {
        return 0.0;
    }


    @Override
    public Inventario getRecord(IDbConnection db, HashMap<String, Object> options) {
        List<Inventario> list = getList(db, options);
        if (list.size()>0) return list.get(0);
        return null;
    }

    @Override
    public boolean insertRecord(IDbConnection db, Inventario record, HashMap<String, Object> options) {
        PostgresDbConnection postgresDb = (PostgresDbConnection) db;
        int insertedRecords = 0;

        try {
            if (!postgresDb.isOpen()){
                postgresDb.open(false);
                localOpen = true;
            }

            String sql = "INSERT INTO public.inventarios(id_alimento, accion, cantidad, fecha) "
                    + "VALUES ("
                    + record.getId_alimento() + ", '"
                    + record.getAccion() + "', "
                    + record.getCantidad() + ", '"
                    + record.getFecha() + "')";
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
    public boolean updateRecord(IDbConnection db, Inventario record, HashMap<String, Object> options) {
        PostgresDbConnection postgresDb = (PostgresDbConnection) db;
        int updatedRecords = 0;

        try {
            if (!postgresDb.isOpen()){
                postgresDb.open(false);
                localOpen = true;
            }

            String sql = "UPDATE public.inventarios "
                    + "SET cantidad = " + record.getCantidad()
                    + "WHERE id_alimento = '" + record.getId_alimento();

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
    public boolean updateInsumo(IDbConnection db, Inventario record, HashMap<String, Object> options, double cantidad_disponible) {
        return false;
    }


    @Override
    public boolean deleteRecord(IDbConnection db, Inventario record, HashMap<String, Object> options) {
        PostgresDbConnection postgresDb = (PostgresDbConnection) db;
        int deletedRecords = 0;

        try {
            if (!postgresDb.isOpen()){
                postgresDb.open(false);
                localOpen = true;
            }

            String sql = "DELETE FROM public.inventarios "
                    + "WHERE id_alimento = '" + record.getId_alimento() + "'";

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
}