import React from 'react';
import { Outlet } from 'react-router-dom';
import Sidebar from './Sidebar';
import Navbar from './Navbar';

const Layout = () => {
    return (
        <div className="layout-wrapper">
            <Sidebar />

            <main className="main-content">
                <Navbar />

                <section className="page-container">
                    <div className="content-card">
                        <Outlet />
                    </div>
                </section>
            </main>
        </div>
    );
};

export default Layout;