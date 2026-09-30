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
package fr.insa.toto.webui.session;

import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;

import fr.insa.toto.webui.erreurs.UtilisateurNonConnecte;

/**
 * On utilise la possibilité de fournir une méthode par défaut à cette interface
 * pour rediriger vers une page d'erreur si l'utilisateur accède directement
 * (par son url) à une page à laquelle il n'a pas droit.
 * <p>
 * Utilisation typique : 
 * <pre> {@code 
 * @Route(value = "uneUrl",layout = MainLayout.class)
 * public class UnePageReserveeAuxUtilisateursConnectes extends VerticalLayout implements OnlyConnected 
 * ...
 * } </pre>
 * </p>
 * @author francois
 */
public interface OnlyConnected extends BeforeEnterObserver {
    
    @Override
    default public void beforeEnter(BeforeEnterEvent event) {
//        Notification.show("user test : " + (SessionInfo.curUser().isPresent() ? SessionInfo.curUser().get().getSurnom() : "not connected" ));
        if (! SessionInfo.userConnected()) {
            event.rerouteTo(UtilisateurNonConnecte.class);
        }
        
    }
    
}
