import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss']
})
export class SidebarComponent implements OnInit {
  menuItems = [
    { path: '/dashboard', title: 'Dashboard', icon: 'dashboard', class: '' },
    { path: '/contracts', title: 'Contratti', icon: 'description', class: '' },
    { path: '/covenants', title: 'Covenant', icon: 'gavel', class: '' },
    { path: '/monitoring', title: 'Monitoraggio', icon: 'assessment', class: '' },
    { path: '/reports', title: 'Reportistica', icon: 'bar_chart', class: '' },
    { path: '/notifications', title: 'Notifiche', icon: 'notifications', class: '' },
    { path: '/admin', title: 'Amministrazione', icon: 'settings', class: '' }
  ];

  constructor() { }

  ngOnInit(): void {
  }
}
