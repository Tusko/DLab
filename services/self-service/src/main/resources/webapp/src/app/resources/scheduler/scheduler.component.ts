/***************************************************************************

Copyright (c) 2018, EPAM SYSTEMS INC

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

****************************************************************************/

import { Component, OnInit, ViewChild, Output, EventEmitter, ViewEncapsulation, ChangeDetectorRef } from '@angular/core';
import { FormGroup, FormControl, FormArray, FormBuilder } from '@angular/forms';

import { DateAdapter, MAT_DATE_FORMATS, MAT_DATE_LOCALE } from '@angular/material/core';
import { MomentDateAdapter } from '@angular/material-moment-adapter';

import * as _moment from 'moment';
import { HTTP_STATUS_CODES } from './../../core/util';

import { SchedulerService } from './../../core/services';
import { SchedulerModel, WeekdaysModel } from './scheduler.model';

@Component({
  selector: 'dlab-scheduler',
  templateUrl: './scheduler.component.html',
  styleUrls: ['./scheduler.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class SchedulerComponent implements OnInit {
  public model: SchedulerModel;
  public selectedWeekDays: WeekdaysModel = new WeekdaysModel(false, false, false, false, false, false, false);
  public notebook: any;
  public infoMessage: boolean = false;
  public date_format: string = 'YYYY-MM-DD';
  public timeFormat: string = 'HH:mm';
  public weekdays: string[] = ['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'];
  public schedulerForm: FormGroup;
  public destination: any;
  public startTime = { hour: 9, minute: 0, meridiem: 'AM' };
  public endTime = { hour: 7, minute: 0, meridiem: 'PM' };

  @ViewChild('bindDialog') bindDialog;
  @ViewChild('resourceSelect') resource_select;
  @Output() buildGrid: EventEmitter<{}> = new EventEmitter();

  constructor(
    private formBuilder: FormBuilder,
    private schedulerService: SchedulerService,
    private changeDetector : ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.bindDialog.onClosing = () => this.resetDialog();
  }

  public open(param, notebook): void {
    this.notebook = notebook;
    this.getResourcesList();

    if (!this.bindDialog.isOpened)
      this.model = new SchedulerModel(
        (response: Response) => {
          if (response.status === HTTP_STATUS_CODES.OK) {
            this.close();
            this.buildGrid.emit();
          }
        },
        error => {},
        () => {
          this.bindDialog.open(param);
          this.formInit();

          this.changeDetector.detectChanges();
          this.resource_select && this.resource_select.setDefaultOptions(this.getResourcesList(), this.notebook.name, 'destination', 'title', 'array');

          this.destination = this.notebook;
          this.selectedWeekDays.setDegault();
          this.getExploratorySchedule(this.notebook.name);
        },
        this.schedulerService
      );
  }

  public onUpdate($event) {
    this.destination = $event.model.value;
    this.selectedWeekDays.setDegault();

    (this.destination.type === 'СOMPUTATIONAL')
      ? this.getExploratorySchedule(this.notebook.name, this.destination.name) 
      : this.getExploratorySchedule(this.notebook.name);
  }

  public onDaySelect($event, day) {
    this.selectedWeekDays[day.toLowerCase()] = $event.checked;
    this.checkSelectedDays();
  }

  public checkSelectedDays() {
    this.infoMessage = false;

    if (!Object.keys(this.selectedWeekDays).some(el => this.selectedWeekDays[el])) {
      this.infoMessage = true;
    }
  }

  public scheduleInstance_btnClick(data) {
    const selectedDays = Object.keys(this.selectedWeekDays);
    const parameters = {
      begin_date: _moment(data.startDate).format(this.date_format),
      finish_date: _moment(data.finishDate).format(this.date_format),
      start_time: this.convertTimeFormat(this.startTime),
      end_time: this.convertTimeFormat(this.endTime),
      days_repeat: selectedDays.filter(el => Boolean(this.selectedWeekDays[el])).map(day => day.toUpperCase()),
      timezone_offset: _moment().format('Z')
    };

    (this.destination.type === 'СOMPUTATIONAL')
      ? this.model.confirmAction(this.notebook.name, parameters, this.destination.name)
      : this.model.confirmAction(this.notebook.name, parameters);
  }

  public close(): void {
    if (this.bindDialog.isOpened) this.bindDialog.close();

    this.resetDialog();
  }

  private getResourcesList() {
    this.notebook.type = 'EXPLORATORY';
    this.notebook.title = `${ this.notebook.name } <em>notebook</em>`;
    return [this.notebook].concat(this.notebook.resources
      .filter(item => item.status === 'running')
      .map(item => {
        item['name'] = item.computational_name;
        item['title'] = `${ item.computational_name } <em>cluster</em>`;
        item['type'] = 'СOMPUTATIONAL';
        return item;
      }));
  }

  private formInit(start?, end?) {
    this.schedulerForm = this.formBuilder.group({
      startDate: start ? _moment(start).format() : _moment(new Date()).format(),
      finishDate: end ? _moment(end).format() : _moment(new Date()).add(1, 'days').format()
    });
  }

  private getExploratorySchedule(resource, resource2?) {
    this.schedulerService.getExploratorySchedule(resource, resource2).subscribe(
      (params: any) => {
        if (params) {
          params.days_repeat.filter(
            key => (this.selectedWeekDays[key.toLowerCase()] = true)
          );
          this.checkSelectedDays();

          this.startTime = this.convertTimeFormat(params.start_time);
          this.endTime = this.convertTimeFormat(params.end_time);

          this.formInit(params.begin_date, params.finish_date);
        }
      },
      error => {}
    );
  }

  private convertTimeFormat(time24: any) {

    let result;
    if (typeof time24 === 'string') {
      let spl = time24.split(':');

      result = {
        hour: +spl[0] % 12 || 12,
        minute: +spl[1],
        meridiem: +spl[0] < 12 || +spl[0] === 24 ? 'AM' : 'PM'
      };
    } else {
      let hours = time24.hour;
      let minutes = (time24.minute < 10) ? '0' + time24.minute : time24.minute;

      if (time24.meridiem == 'PM' && time24.hour < 12) hours = time24.hour + 12;
      if (time24.meridiem == 'AM' &&  time24.hour == 12) hours = time24.hour - 12;
      hours = hours < 10 ? '0' + hours : hours;

      result = `${hours}:${minutes}`;
    }
    return result;
  }

  private resetDialog() {
    this.infoMessage = false;
    this.startTime = this.convertTimeFormat('00:00');
    this.endTime = this.convertTimeFormat('00:00');
  }
}
