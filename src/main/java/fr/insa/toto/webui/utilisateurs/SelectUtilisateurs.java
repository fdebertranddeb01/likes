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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import fr.insa.toto.model.Utilisateur;

/**
 *
 * @author francois
 */
public class SelectUtilisateurs extends HorizontalLayout {
    
    private List<Utilisateur> allElems;
    private List<Utilisateur> selectedElems;
    
    private List<Utilisateur> nonSelectedElems;
    
    private Grid<Utilisateur> gNonselected;
    private Grid<Utilisateur> gSelected;
    private Button bAdd;
    private Button bRemove;
    
    public SelectUtilisateurs(List<Utilisateur> allElems,List<Utilisateur> selectedElems) {
        this.allElems = allElems;
        this.selectedElems = new ArrayList<>(selectedElems);
        this.nonSelectedElems = new ArrayList<>(allElems);
        this.nonSelectedElems.removeAll(this.selectedElems);
        
        this.gSelected = new Grid<>(this.selectedElems);
        this.gSelected.addColumn(Utilisateur::getSurnom);
        this.gSelected.setSelectionMode(Grid.SelectionMode.MULTI);
        this.gSelected.addThemeVariants(GridVariant.LUMO_COMPACT);
        this.gNonselected = new Grid<>(this.nonSelectedElems);
        this.gNonselected.addColumn(Utilisateur::getSurnom);
        this.gNonselected.setSelectionMode(Grid.SelectionMode.MULTI);
        this.gNonselected.addThemeVariants(GridVariant.LUMO_COMPACT);
        this.setWidthFull();
        this.bAdd = new Button(">>AJOUTE>>");
        this.bAdd.addClickListener((t) -> {
            this.doAdd();
        });
    
        this.bRemove = new Button("<<ENLEVE<<");
        this.bRemove.addClickListener((t) -> {
            this.doRemove();
        });
        VerticalLayout vlBoutons = new VerticalLayout(this.bAdd,this.bRemove);
        VerticalLayout vlNonSelected = new VerticalLayout(new H3("Non selectionnés"),this.gNonselected);
        VerticalLayout vlSelected = new VerticalLayout(new H3("Selectionnés"),this.gSelected);
        this.add(vlNonSelected,vlBoutons,vlSelected);
    }

    private void doAdd() {
        Set<Utilisateur> toAdd = this.gNonselected.getSelectedItems();
        this.nonSelectedElems.removeAll(toAdd);
        this.selectedElems.addAll(toAdd);
        this.refreshGrids();
    }

    private void doRemove() {
        Set<Utilisateur> toRemove = this.gSelected.getSelectedItems();
        this.selectedElems.removeAll(toRemove);
        this.nonSelectedElems.addAll(toRemove);
        this.refreshGrids();
    }

    private void refreshGrids() {
        this.gSelected.setItems(this.selectedElems);
        this.gNonselected.setItems(this.nonSelectedElems);
    }
    
    public List<Utilisateur> getCurrentSelection() {
        return this.selectedElems;
    }

    public List<Utilisateur> getAllElemps() {
        return this.allElems;
    }
    
}
