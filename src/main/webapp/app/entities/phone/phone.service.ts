import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_FORMAT } from 'app/shared/constants/input.constants';
import { map } from 'rxjs/operators';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared';
import { IPhone } from 'app/shared/model/phone.model';

type EntityResponseType = HttpResponse<IPhone>;
type EntityArrayResponseType = HttpResponse<IPhone[]>;

@Injectable({ providedIn: 'root' })
export class PhoneService {
    public resourceUrl = SERVER_API_URL + 'api/phones';

    constructor(private http: HttpClient) {}

    create(phone: IPhone): Observable<EntityResponseType> {
        const copy = this.convertDateFromClient(phone);
        return this.http
            .post<IPhone>(this.resourceUrl, copy, { observe: 'response' })
            .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
    }

    update(phone: IPhone): Observable<EntityResponseType> {
        const copy = this.convertDateFromClient(phone);
        return this.http
            .put<IPhone>(this.resourceUrl, copy, { observe: 'response' })
            .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
    }

    find(id: number): Observable<EntityResponseType> {
        return this.http
            .get<IPhone>(`${this.resourceUrl}/${id}`, { observe: 'response' })
            .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
    }

    query(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http
            .get<IPhone[]>(this.resourceUrl, { params: options, observe: 'response' })
            .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
    }

    delete(id: number): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    protected convertDateFromClient(phone: IPhone): IPhone {
        const copy: IPhone = Object.assign({}, phone, {
            releaseDate: phone.releaseDate != null && phone.releaseDate.isValid() ? phone.releaseDate.format(DATE_FORMAT) : null
        });
        return copy;
    }

    protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
        if (res.body) {
            res.body.releaseDate = res.body.releaseDate != null ? moment(res.body.releaseDate) : null;
        }
        return res;
    }

    protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
        if (res.body) {
            res.body.forEach((phone: IPhone) => {
                phone.releaseDate = phone.releaseDate != null ? moment(phone.releaseDate) : null;
            });
        }
        return res;
    }
}
