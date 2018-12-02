import { Moment } from 'moment';

export interface IPhone {
    id?: number;
    name?: string;
    brand?: string;
    price?: number;
    releaseDate?: Moment;
}

export class Phone implements IPhone {
    constructor(public id?: number, public name?: string, public brand?: string, public price?: number, public releaseDate?: Moment) {}
}
