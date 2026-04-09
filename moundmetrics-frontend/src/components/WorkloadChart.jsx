import React, { useRef, useMemo } from 'react';
import { Bar } from 'react-chartjs-2';
import {
    Chart as ChartJS,
    CategoryScale,
    LinearScale,
    BarElement,
    Title,
    Tooltip,
    Legend,
    Filler
} from 'chart.js';
import './WorkloadChart.css';

ChartJS.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend, Filler);

const WorkloadChart = ({ appearances }) => {
    const chartRef = useRef(null);

    // 🔥 Filtrar solo apariciones con lanzamientos > 0
    const aparicionesValidas = useMemo(() => {
        if (!appearances) return [];
        return appearances.filter(app => app.pitchesThrown > 0);
    }, [appearances]);

    if (aparicionesValidas.length === 0) {
        return (
            <div className="chart-card empty">
                <div className="empty-state">
                    <p>No hay datos de lanzamientos disponibles</p>
                </div>
            </div>
        );
    }

    // Formatear fechas: "7 abr"
    const formatDate = (dateStr) => {
        const date = new Date(dateStr + 'T00:00:00'); // evitar problemas de zona horaria
        return date.toLocaleDateString('es-ES', { day: 'numeric', month: 'short' });
    };

    const data = {
        labels: aparicionesValidas.map(app => formatDate(app.gameDate)),
        datasets: [
            {
                label: 'Lanzamientos',
                data: aparicionesValidas.map(app => app.pitchesThrown),
                backgroundColor: 'rgba(59, 130, 246, 0.7)',
                hoverBackgroundColor: 'rgba(59, 130, 246, 1)',
                borderColor: '#2563eb',
                borderWidth: 1,
                borderRadius: 6,
                borderSkipped: false,
            },
        ],
    };

    const options = {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
            legend: { display: false },
            tooltip: {
                backgroundColor: '#1e293b',
                padding: 12,
                titleFont: { size: 14, weight: 'bold' },
                bodyFont: { size: 13 },
                cornerRadius: 8,
                displayColors: false
            },
        },
        scales: {
            x: {
                grid: { display: false },
                ticks: {
                    color: '#64748b',
                    font: { family: 'Inter, sans-serif', size: 11 }
                }
            },
            y: {
                beginAtZero: true,
                grid: { color: '#f1f5f9', drawBorder: false },
                ticks: {
                    color: '#64748b',
                    font: { family: 'Inter, sans-serif', size: 11 },
                    stepSize: 20
                }
            },
        },
    };

    const maxPitches = Math.max(...aparicionesValidas.map(a => a.pitchesThrown));

    return (
        <div className="chart-card">
            <div className="chart-header">
                <div>
                    <h4>Carga de Trabajo</h4>
                    <p className="subtitle">Picheos por salida</p>
                </div>
                <div className="max-value">
                    Max: {maxPitches}
                </div>
            </div>
            <div className="chart-container">
                <Bar ref={chartRef} data={data} options={options} />
            </div>
        </div>
    );
};

export default WorkloadChart;