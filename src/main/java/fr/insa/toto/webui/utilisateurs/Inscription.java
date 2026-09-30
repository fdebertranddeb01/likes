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

import java.sql.Connection;
import java.sql.SQLException;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import fr.insa.beuvron.utils.database.ConnectionPool;
import fr.insa.toto.model.Utilisateur;
import fr.insa.toto.webui.MainLayout;

/**
 *
 * @author francois
 */
@Route(value = "utilisateurs/inscription",layout = MainLayout.class)
@PageTitle("Likes")
public class Inscription extends FormLayout{

    private TextField surnom;
    private PasswordField password;
    private Button save;
    
    public Inscription() {
        this.surnom = new TextField("surnom");
        this.password = new PasswordField("password");
        this.save = new Button("save");
        this.save.addClickListener((t) -> {
            this.doSave();
        });

        this.setAutoResponsive(true);
        this.addFormRow(this.surnom,this.password);
        this.addFormRow(this.save);
    }
    
    public void doSave() {
        try (Connection con = ConnectionPool.getConnection()) {
            String surnom = this.surnom.getValue();
            String pass = this.password.getValue();
            int role = 2;
            Utilisateur u = new Utilisateur(surnom, pass, role);
            u.saveInDB(con);  
            Notification.show("utilisateur "+ surnom + " créé");
        } catch (SQLException ex) {
            Notification.show("Problème : " + ex.getLocalizedMessage());
        }
    }

}
