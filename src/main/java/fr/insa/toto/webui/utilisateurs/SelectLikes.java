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
package fr.insa.toto.webui.utilisateurs;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import fr.insa.beuvron.utils.database.ConnectionPool;
import fr.insa.toto.model.Utilisateur;
import fr.insa.toto.webui.MainLayout;
import fr.insa.toto.webui.session.OnlyConnected;
import fr.insa.toto.webui.session.SessionInfo;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

/**
 *
 * @author francois
 */
@Route(value = "utilisateurs/selectLikes", layout = MainLayout.class)
@PageTitle("Likes")
public class SelectLikes extends VerticalLayout implements OnlyConnected {

    private SelectUtilisateurs lsUser;
    private Button bConfirm;

    public SelectLikes() {
        this.add(new H3("Merci de selectionner les utilisateurs que vous appréciez"));
        try (Connection con = ConnectionPool.getConnection()) {
            Optional<Utilisateur> ocurU = SessionInfo.curUser();
            if (ocurU.isPresent()) {
                Utilisateur curU = ocurU.get();
                this.lsUser = new SelectUtilisateurs(Utilisateur.tousLesUtilisateur(con), Utilisateur.utilisateursAppreciesPar(con, curU));
                this.bConfirm = new Button("Sauvegarder");
                this.bConfirm.addClickListener((t) -> {
                    try (Connection con2 = ConnectionPool.getConnection()) {
                        Utilisateur.changeApprecie(con2, curU, this.lsUser.getCurrentSelection());
                    } catch (SQLException ex) {
                        Notification.show("Problème : " + ex.getLocalizedMessage());
                    }
                });
                this.add(this.lsUser, this.bConfirm);
            } else {
                this.add(new H2("pas d'utilisateur courant"));
            }
        } catch (SQLException ex) {
            Notification.show(ex.getLocalizedMessage());
        }

    }

}
