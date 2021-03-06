import { Observable } from 'rxjs/Rx';
import { Injectable } from '@angular/core';
import { Http, Headers, Response } from "@angular/http";
import * as io from 'socket.io-client';

import 'rxjs/Rx';

@Injectable()
export class ChatService {

  private _headers: Headers;
  private _url: string = '';
  server = null;

  name: string = '';
  logTime: Date;

  constructor(private _http: Http) { 

    while(this.name == '')
      this.name = prompt("What is your name?");

    this.server = io('http://localhost:3000');  

    this.logTime = new Date();

    this._headers = new Headers();
    this._headers.append('Content-Type', 'application/json');

    this._url = "http://localhost:3000/messages";
  }

  public getMessages(): Observable<Array<any>> {
    return this._http.get(this._url).map(res => res.json());
  }

   public sendMessage(message: any): Observable<Response> {
    return this._http.post(this._url, JSON.stringify(message), { headers: this._headers });
  }

}
