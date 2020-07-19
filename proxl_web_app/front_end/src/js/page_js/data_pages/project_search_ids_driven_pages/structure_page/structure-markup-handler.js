"use strict";

export class StructureMarkupHandler {

    constructor() {
        this._proteinsMarkedOnStructure = { };
        this._lastInsertId = 0;
    }

    /**
     * Get the next id to use
     *
     * @returns {number}
     */
    getNewId() {
        this._lastInsertId++;
        return this._lastInsertId;
    }

    /**
     * Delete the protein annotation data with the given id
     *
     * @param id
     */
    deleteStructureProteinMarkup({id}) {
        delete this._proteinsMarkedOnStructure[id];
    }

    /**
     * Add the given protein annotation data
     *
     * @param proteinId
     * @param start
     * @param end
     * @param color
     */
    addProteinColorAnnotation({proteinId, start, end, color}) {
        const newId = this.getNewId();
        const structureMarkupObject = {
            "proteinId" : proteinId,
            "start" : start,
            "end" : end,
            "color" : color,
            "id" : newId
        }

        this._proteinsMarkedOnStructure[newId] = structureMarkupObject;

        console.log(this);
    }

    /**
     * Replace the protein annotation with the given id with the given data
     * @param id
     * @param proteinId
     * @param start
     * @param end
     * @param color
     */
    updateProteinColorAnnotation({id, proteinId, start, end, color}) {
        const structureMarkupObject = {
            "proteinId" : proteinId,
            "start" : start,
            "end" : end,
            "color" : color,
            "id" : id
        }

        this._proteinsMarkedOnStructure[id] = structureMarkupObject;
    }

    /**
     * Get all the protein color anotation objects in order of id
     *
     * @returns {[]}
     */
    getOrderedProteinColorAnnotations() {

        const sortedIds = this.getSortedIds();

        let objects = [];

        for (const id of sortedIds) {
            objects.push(this._proteinsMarkedOnStructure[id]);
        }

        return objects;
    }

    /**
     * Get all the ids sorted from smallest to largest
     *
     * @returns {this}
     */
    getSortedIds() {
        let ids = [];

        for (const id of Object.keys(this._proteinsMarkedOnStructure)) {
            ids.push(id);
        }

        ids = ids.sort();
        return ids;
    }

    /**
     * Get the data structure to use for storing the state of this object in the URL hash
     *
     * @returns {[]}
     */
    getDataStructureForHash() {
        let ds = [];

        for(const markup of this.getSortedIds()) {
            let item = "";
            item += markup.proteinId + "-";
            item += markup.start + "-";
            item += markup.end + "-";
            item += markup.color;

            ds.push(item);
        }

        return ds;
    }


}