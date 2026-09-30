/*
Copyright 2000- Francois de Bertrand de Beuvron

This file is part of CoursBeuvron.

CoursBeuvron is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

CoursBeuvron is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with CoursBeuvron.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.insa.toto.model;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import fr.insa.beuvron.utils.ConsoleFdB;

/**
 * Une petite classe "miroir" pour représenter un utilisateur de l'application.
 * <p>
 * convention pour les identifiants :
 * <ul>
 * <li>-1 signifie que l'objet n'a pas encore été sauvegardé dans la base de
 * données.</li>
 * <li>un identifiant positif est celui attribué par la base de données.</li>
 * </ul>
 * 
 * @author francois
 */
public class Utilisateur implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id = -1;
    private String surnom;
    private String pass;
    private int role;

    /**
     * pour nouvel utilisateur en mémoire
     */
    public Utilisateur(String surnom, String pass, int role) {
        this(-1, surnom, pass, role);
    }

    /**
     * pour utilisateur récupéré de la base de données
     */
    public Utilisateur(int id, String surnom, String pass, int role) {
        this.id = id;
        this.surnom = surnom;
        this.pass = pass;
        this.role = role;
    }

    @Override
    public String toString() {
        return "Utilisateur{" + "id=" + this.getId() + "surnom=" + surnom + ", role=" + role + '}';
    }

    /** pour gérer l'égalité des objets en tenant compte de leur état de sauvegarde dans la base de données :
     * - si les deux objets ont un id positif, on compare les id
     * - si les deux objets ont un id négatif, on compare les références (super.equals)
     * - si un objet a un id positif et l'autre un id négatif, ils ne sont pas égaux
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final Utilisateur other = (Utilisateur) obj;
        if (this.getId() == -1) {
            if (other.getId() == -1) {
                return super.equals(obj);
            } else {
                return false;
            }
        } else {
            if (other.getId() == -1) {
                return false;
            } else {
                return this.getId() == other.getId();
            }
        }
    }

    @Override
    public int hashCode() {
        if (this.getId() == -1) {
            return super.hashCode();
        } else {
            return this.getId();
        }
    }

    public void saveInDB(Connection con) throws SQLException {
        if (this.getId() != -1) {
            throw new SQLException("Utilisateur déjà sauvegardé dans la base de données");
        }
        try (PreparedStatement insert = con.prepareStatement(
                "insert into utilisateur (surnom,pass,role) values (?,?,?)",
                PreparedStatement.RETURN_GENERATED_KEYS)) {
            insert.setString(1, this.getSurnom());
            insert.setString(2, this.getPass());
            insert.setInt(3, getRole());
            insert.executeUpdate();
            ResultSet rs = insert.getGeneratedKeys();
            if (rs.next()) {
                this.id = rs.getInt(1);
            } else {
                throw new SQLException("aucun id généré");
            }
        }
    }

    /**
     * supprime l'utilisateur de la BdD. Attention : supprime d'abord les
     * éventuelles dépendances.
     *
     * @param con
     * @throws SQLException
     */
    public void deleteInDB(Connection con) throws SQLException {
        if (this.getId() == -1) {
            throw new SQLException("Utilisateur non sauvegardé dans la base de données");
        }
        try {
            con.setAutoCommit(false);
            try (PreparedStatement pst = con.prepareStatement(
                    "delete from pratique where idutilisateur = ?")) {
                pst.setInt(1, this.getId());
                pst.executeUpdate();
            }
            try (PreparedStatement pst = con.prepareStatement(
                    "delete from apprecie where u1 = ?")) {
                pst.setInt(1, this.getId());
                pst.executeUpdate();
            }
            try (PreparedStatement pst = con.prepareStatement(
                    "delete from apprecie where u2 = ?")) {
                pst.setInt(1, this.getId());
                pst.executeUpdate();
            }

            try (PreparedStatement pst = con.prepareStatement(
                    "delete from utilisateur where id = ?")) {
                pst.setInt(1, this.getId());
                pst.executeUpdate();
            }
            this.id = -1;
            con.commit();
        } catch (SQLException ex) {
            con.rollback();
            throw ex;
        } finally {
            con.setAutoCommit(true);
        }
    }

    /**
     * suppose que le resultset contient bien une table d'utilisateurs.
     *
     * @param users
     * @return
     */
    private static List<Utilisateur> fromResultSetToList(ResultSet users) throws SQLException {
        List<Utilisateur> res = new ArrayList<>();
        while (users.next()) {
            res.add(new Utilisateur(users.getInt("id"), users.getString("surnom"),
                    users.getString("pass"), users.getInt("role")));
        }
        return res;

    }

    public static List<Utilisateur> tousLesUtilisateur(Connection con) throws SQLException {
        try (PreparedStatement pst = con.prepareStatement("select id,surnom,pass,role from utilisateur")) {
            try (ResultSet allU = pst.executeQuery()) {
                return fromResultSetToList(allU);
            }
        }
    }

    public static List<Utilisateur> utilisateursAppreciesPar(Connection con, Utilisateur u1) throws SQLException {
        try (PreparedStatement pst = con.prepareStatement(
                "select id,surnom,pass,role \n"
                        + " from utilisateur join apprecie on apprecie.u2 = utilisateur.id \n"
                        + " where apprecie.u1 = ?")) {
            pst.setInt(1, u1.getId());
            try (ResultSet allU = pst.executeQuery()) {
                return fromResultSetToList(allU);
            }
        }
    }

    public static Optional<Utilisateur> findBySurnomPass(Connection con, String surnom, String pass)
            throws SQLException {
        try (PreparedStatement pst = con.prepareStatement(
                "select id,role from utilisateur where surnom = ? and pass = ?")) {
            pst.setString(1, surnom);
            pst.setString(2, pass);
            ResultSet res = pst.executeQuery();
            if (res.next()) {
                int id = res.getInt(1);
                int role = res.getInt(2);
                return Optional.of(new Utilisateur(id, surnom, pass, role));
            } else {
                return Optional.empty();
            }

        }
    }

    public static void changeApprecie(Connection con, Utilisateur u, List<Utilisateur> appreciesParU)
            throws SQLException {
        try {
            con.setAutoCommit(false);
            // je supprime tous les anciens
            try (PreparedStatement pst = con.prepareStatement("delete from apprecie where u1 = ?")) {
                pst.setInt(1, u.getId());
                pst.executeUpdate();
            }
            // je crée avec les nouveaux
            try (PreparedStatement pst = con.prepareStatement("insert into apprecie (u1,u2) values (?,?)")) {
                pst.setInt(1, u.getId());
                for (var u2 : appreciesParU) {
                    pst.setInt(2, u2.getId());
                    pst.executeUpdate();
                }
            }
            con.commit();
        } catch (SQLException ex) {
            con.rollback();
            throw ex;
        } finally {
            con.setAutoCommit(true);
        }
    }

    public static Utilisateur entreeConsole() {
        String nom = ConsoleFdB.entreeString("surnom de l'utilisateur : ");
        String pass = ConsoleFdB.entreeString("password : ");
        return new Utilisateur(nom, pass, 2);
    }

    public int getId() {
        return id;
    }

    /**
     * @return the surnom
     */
    public String getSurnom() {
        return surnom;
    }

    /**
     * @param surnom the surnom to set
     */
    public void setSurnom(String surnom) {
        this.surnom = surnom;
    }

    /**
     * @return the pass
     */
    public String getPass() {
        return pass;
    }

    /**
     * @param pass the pass to set
     */
    public void setPass(String pass) {
        this.pass = pass;
    }

    /**
     * @return the role
     */
    public int getRole() {
        return role;
    }

    /**
     * @param role the role to set
     */
    public void setRole(int role) {
        this.role = role;
    }

}
