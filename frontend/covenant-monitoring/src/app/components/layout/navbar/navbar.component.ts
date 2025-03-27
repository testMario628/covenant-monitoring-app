import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent implements OnInit {
  public isCollapsed = true;
  public searchQuery = '';
  public unreadNotifications = 0;

  constructor() { }

  ngOnInit(): void {
    // In un'implementazione reale, qui si recupererebbe il conteggio delle notifiche non lette
    this.unreadNotifications = 5;
  }

  toggleSidebar(): void {
    // Implementazione per mostrare/nascondere la sidebar su dispositivi mobili
    const body = document.getElementsByTagName('body')[0];
    body.classList.toggle('nav-open');
  }

  search(): void {
    // Implementazione della ricerca globale
    console.log('Searching for:', this.searchQuery);
    // Qui si implementerebbe la logica di ricerca reale
  }
}
