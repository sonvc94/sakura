import { NgModule } from '@angular/core';

import { SakuraSharedLibsModule, JhiAlertComponent, JhiAlertErrorComponent } from './';

@NgModule({
    imports: [SakuraSharedLibsModule],
    declarations: [JhiAlertComponent, JhiAlertErrorComponent],
    exports: [SakuraSharedLibsModule, JhiAlertComponent, JhiAlertErrorComponent]
})
export class SakuraSharedCommonModule {}
