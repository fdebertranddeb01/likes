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
package fr.insa.toto.webui;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import fr.insa.beuvron.utils.database.ConnectionPool;
import fr.insa.beuvron.vaadin.utils.dataGrid.ResultSetGrid;
import fr.insa.toto.model.BdDTest;
import fr.insa.toto.model.GestionSchema;

/**
 *
 * @author francois
 */
@Route(value = "", layout = MainLayout.class)
@PageTitle("Likes")
public class VuePrincipale extends VerticalLayout {

    public VuePrincipale() {
        this.add(new H2("Bienvenu dans Likes"));
        this.add(new Paragraph("une superbe application"));
        try (Connection con = ConnectionPool.getConnection()) {
            PreparedStatement pst = con.prepareStatement(
                    "select surnom, (\n"
                    + "  select count(*) from apprecie where u2 = utilisateur.id\n"
                    + ") as popularite\n"
                    + "from utilisateur \n"
                    + "order by popularite desc");
            this.add(new ResultSetGrid(pst));
        } catch (SQLException ex) {
            Notification.show("Problème : " + ex.getLocalizedMessage());
        }
        this.add(new H2("Normalement pas présent dans une vraie application"));
        this.add(new Paragraph("la base de données est en mémoire dans cette application de test"));
        this.add(new Paragraph("elle doit être réinitialisée à chaque démarrage"));
        Button razBdd = new Button("Réinitialiser la base de données");
        razBdd.addClickListener((e) -> {
            try (Connection con = ConnectionPool.getConnection()) {
                GestionSchema.razBdd(con);
                BdDTest.createBdDTestV2(con);
                Notification.show("Base de données réinitialisée");
            } catch (SQLException ex) {
                Notification.show("Problème : " + ex.getLocalizedMessage());
            }
        });
        this.add(razBdd);
    }

}
