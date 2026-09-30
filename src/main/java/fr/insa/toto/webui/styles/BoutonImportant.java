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
package fr.insa.toto.webui.styles;

import com.vaadin.flow.component.button.Button;

/**
 * Essai sur les styles CSS.
 * <p>
 * deux possibilités :
 * <ul>
 *   <li> fixer directement sur un composant avec getStyle + set</li>
 *   <li> utiliser le fichier de style CSS : dans ce cas, le plus simple est
 *        d'associer une classe CSS au composant
 *   </li> 
 * </ul>
 * </p>
 * @author francois
 */
public class BoutonImportant extends Button{
    
    public BoutonImportant(String message) {
        super(message);
        // fixer la classe permet facilement de modifier la présentation CSS
        // en modifiant le fichier de style
        // le fichier de style est défini par l'annotation @Theme de la classe Application
        // il doit correspondre à un dossier dans src/main/frontend/themes
        // ce dossier doit contenir un fichier styles.css
        this.setClassName("boutonImportant");
        // on peut aussi fixer directement certains attributs css :
        // getStyle permet de récupérer la description css associée à un composant
        // puis on fait des set pour imiter la syntaxe css clé : valeur
        // ci dessous, cela correspond à ajouter font-size : 1.5em; au css
        this.getStyle().set("font-size", "1.5em");
    }
    
}
