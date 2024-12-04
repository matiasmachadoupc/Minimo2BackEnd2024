package edu.upc.dsa.orm;

import edu.upc.dsa.models.User;
import edu.upc.dsa.orm.Session;
import edu.upc.dsa.orm.util.ObjectHelper;
import edu.upc.dsa.orm.util.QueryHelper;
import edu.upc.dsa.util.RandomUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class SessionImpl implements Session {
    private final Connection conn;

    public SessionImpl(Connection conn) {
        this.conn = conn;
    }

    public int save(Object entity) {


        // INSERT INTO Partida () ()
        String insertQuery = QueryHelper.createQueryINSERT(entity);
        // INSERT INTO User (ID, lastName, firstName, address, city) VALUES (0, ?, ?, ?,?)


        PreparedStatement pstm = null;

        try {
            if (conn == null || conn.isClosed()) {
                throw new SQLException("Connection is not established or is closed.");
            }
            pstm = conn.prepareStatement(insertQuery);
            int i = 1;

            for (String field: ObjectHelper.getFields(entity)) {
                pstm.setObject(i++, ObjectHelper.getter(entity, field));
            }

            pstm.executeQuery();
            return 1;

        } catch (SQLException e) {

            e.printStackTrace();
            return -1;
        }

    }

    public void close() {

    }

    @Override
    public Object get(Class theClass, Object ID) {
        String sql = QueryHelper.createQuerySELECT(theClass);
        Object entity = null;
        try (PreparedStatement pstm = conn.prepareStatement(sql)) {
            pstm.setObject(1, ID);
            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {
                    entity = theClass.getDeclaredConstructor().newInstance();
                    for (String field : ObjectHelper.getFields(entity)) {
                        if (!field.equals("quantity")) {
                            ObjectHelper.setter(entity, field, rs.getObject(field));
                        }
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return entity;
    }



    public void update(Object object) {
        String updateQuery = QueryHelper.createQueryUPDATE(object);
        try (PreparedStatement pstm = conn.prepareStatement(updateQuery)) {
            int i = 1;
            for (String field : ObjectHelper.getFields(object)) {
                if (!field.equals("id")) {
                    pstm.setObject(i++, ObjectHelper.getter(object, field));
                }
            }
            pstm.setObject(i, ObjectHelper.getter(object, "username"));
            pstm.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(Object object) {

    }

    public List<Object> findAll(Class theClass) {
        List<Object> entities = new ArrayList<>();
        String sql = QueryHelper.createSelectFindAll(theClass);
        try (PreparedStatement pstm = conn.prepareStatement(sql);
             ResultSet rs = pstm.executeQuery()) {
            while (rs.next()) {
                Object entity = theClass.getDeclaredConstructor().newInstance();
                for (String field : ObjectHelper.getFields(entity)) {
                    if (!field.equals("quantity")) {
                        ObjectHelper.setter(entity, field, rs.getObject(field));
                    }
                }
                entities.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return entities;
    }

    public List<Object> findAll(Class theClass, HashMap params) {
        return null;
    }

    public List<Object> query(String query, Class theClass, HashMap params) {
        return null;
    }

    @Override
    public int InsertUserItems(String userID, String itemID, int quantity) {
        RandomUtils random = new RandomUtils();
        String sql = "INSERT INTO user_item (id, user_id, item_id, quantity) VALUES (?, ?, ?, ?)";
        String id = random.getId();
        try (PreparedStatement pstm = conn.prepareStatement(sql)) {
            pstm.setString(1, id);
            pstm.setString(2, userID);
            pstm.setString(3, itemID);
            pstm.setInt(4, quantity);
            return pstm.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }
}