import { Component, OnInit } from '@angular/core';
import {Http, Response } from "@angular/http";
import { HttpClient } from '@angular/common/http';
import 'rxjs/add/operator/map';

@Component({
  selector: 'app-webapi',
  templateUrl: './sights.component.html',
  styleUrls: ['./sights.component.scss']
})
export class SightsComponent  implements OnInit {
 //declaratie
 data: any = {};

 showSpinner:boolean;
 getInfo:boolean;

 constructor(private http: HttpClient) {
 }


 ngOnInit() { this. getSights(); }

   getDataFromAPI(){
    // http://aspcorepycmapi.azurewebsites.net/Sights
    // http://localhost:4201/api/v1/heroes?page=0
     return this.http.get(`http://localhost:4201/api/v1/heroes?page=0`)
     .map(res => res)
   }

   getSights(){
    this.showSpinner = true;
    this.getInfo = false;
     this.getDataFromAPI().subscribe(data => {
       console.log("Data werd ingelezen en in array gestoken");
       console.log(data);
       //data wordt in array gestoken
       this.data = data
       this.getInfo = true;
       this.showSpinner = false;
     })
   }
 
}
