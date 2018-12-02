import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IPhone } from 'app/shared/model/phone.model';

@Component({
    selector: 'jhi-phone-detail',
    templateUrl: './phone-detail.component.html'
})
export class PhoneDetailComponent implements OnInit {
    phone: IPhone;

    constructor(private activatedRoute: ActivatedRoute) {}

    ngOnInit() {
        this.activatedRoute.data.subscribe(({ phone }) => {
            this.phone = phone;
        });
    }

    previousState() {
        window.history.back();
    }
}
