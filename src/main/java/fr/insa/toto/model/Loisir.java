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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Une petite classe "miroir" pour représenter un loisir de l'application.
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
public class Loisir {

    private int id;
    private String nom;
    private String description;

    /**
     * Constructeur pour un Loisir récupéré de la base de données
     * 
     * @param id
     * @param nom
     * @param description
     */
    public Loisir(int id, String nom, String description) {
        this.id = id;
        this.nom = nom;
        this.description = description;
    }

    /**
     * Constructeur pour un Loisir créé en mémoire et pas encore sauvegardé dans la base de données
     * @param nom
     * @param description
     */
    public Loisir(String nom, String description) {
        this(-1, nom, description);
    }

    @Override 
    public String toString() {
        return "Loisir{" + "id=" + id + ", nom=" + nom + ", description=" + description + '}';
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
            throw new SQLException("Loisir déjà sauvegardé dans la base de données");
        }
        try (PreparedStatement insert = con.prepareStatement(
                "insert into loisir (nom,description) values (?,?)",
                PreparedStatement.RETURN_GENERATED_KEYS)) {
            insert.setString(1, this.getNom());
            insert.setString(2, this.getDescription());
            insert.executeUpdate();
            ResultSet rs = insert.getGeneratedKeys();
            if (rs.next()) {
                this.id = rs.getInt(1);
            } else {
                throw new SQLException("Erreur lors de la récupération de l'identifiant généré pour le loisir");
            }
        }
    }

    public void deleteInDB(Connection con) throws SQLException {
        if (this.getId() == -1) {
            throw new SQLException("Loisir non sauvegardé dans la base de données");
        }
        try (PreparedStatement delete = con.prepareStatement(
                "delete from loisir where id = ?")) {
            delete.setInt(1, this.getId());
            int rowsAffected = delete.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Aucun loisir trouvé avec l'identifiant " + this.getId());
            }
            this.id = -1; // Marquer l'objet comme non sauvegardé
        }
    }
    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @return the nom
     */
    public String getNom() {
        return nom;
    }

    /**
     * @param nom the nom to set
     */
    public void setNom(String nom) {
        this.nom = nom;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

}
