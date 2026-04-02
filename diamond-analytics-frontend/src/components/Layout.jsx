import React from 'react';
import { Outlet } from 'react-router-dom'; // Importante para rutas anidadas
import Sidebar from './Sidebar';
import Navbar from './Navbar';

const Layout = () => {
    return (
        <div className="layout-wrapper">
            <Sidebar />
            <main className="main-content">
                <Navbar />
                <section className="page-container">
                    {/* Quitamos la div "content-card" de aquí si queremos
                        que cada página controle su propio fondo blanco,
                        o la dejamos si queremos que TODO tenga el mismo cuadro. */}
                    <div className="content-card">
                        <Outlet /> {/* Aquí es donde React Router inyecta el Dashboard */}
                    </div>
                </section>
            </main>
        </div>
    );
};

export default Layout;