import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import * as moment from 'moment';

import { IPhone } from 'app/shared/model/phone.model';
import { PhoneService } from './phone.service';

@Component({
    selector: 'jhi-phone-update',
    templateUrl: './phone-update.component.html'
})
export class PhoneUpdateComponent implements OnInit {
    phone: IPhone;
    isSaving: boolean;
    releaseDateDp: any;

    constructor(private phoneService: PhoneService, private activatedRoute: ActivatedRoute) {}

    ngOnInit() {
        this.isSaving = false;
        this.activatedRoute.data.subscribe(({ phone }) => {
            this.phone = phone;
        });
    }

    previousState() {
        window.history.back();
    }

    save() {
        this.isSaving = true;
        if (this.phone.id !== undefined) {
            this.subscribeToSaveResponse(this.phoneService.update(this.phone));
        } else {
            this.subscribeToSaveResponse(this.phoneService.create(this.phone));
        }
    }

    private subscribeToSaveResponse(result: Observable<HttpResponse<IPhone>>) {
        result.subscribe((res: HttpResponse<IPhone>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
    }

    private onSaveSuccess() {
        this.isSaving = false;
        this.previousState();
    }

    private onSaveError() {
        this.isSaving = false;
    }
}
