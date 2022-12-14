import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  constructor(private router:Router) { }

  ngOnInit(): void {
  }
  navToLogin(){
    this.router.navigateByUrl('/container/login');
  }
  navAdminLog(){
    this.router.navigateByUrl('adminLog')
  }
  ownerRegister(){
    this.router.navigateByUrl('ownerregister')
  }

}
