/* =========================================================
   AutoCare Pro — Admin Dashboard Charts
   ========================================================= */

document.addEventListener('DOMContentLoaded', () => {
    if (document.getElementById('statusChart')) loadStatusChart();
    if (document.getElementById('typeChart')) loadTypeChart();
});

async function loadStatusChart() {
    try {
        const res = await fetch('/api/admin/charts/bookings-by-status');
        const data = await res.json();
        const ctx = document.getElementById('statusChart');

        new Chart(ctx, {
            type: 'doughnut',
            data: {
                labels: Object.keys(data),
                datasets: [{
                    data: Object.values(data),
                    backgroundColor: ['#f59e0b', '#3b82f6', '#00d9ff', '#22c55e', '#ef4444'],
                    borderWidth: 0,
                    hoverOffset: 8
                }]
            },
            options: {
                responsive: true,
                plugins: {
                    legend: { position: 'bottom', labels: { color: getComputedStyle(document.body).getPropertyValue('--text-secondary') || '#9aa4bd' } }
                },
                cutout: '65%'
            }
        });
    } catch (e) { console.error('Failed to load status chart', e); }
}

async function loadTypeChart() {
    try {
        const res = await fetch('/api/admin/charts/bookings-by-type');
        const data = await res.json();
        const ctx = document.getElementById('typeChart');

        new Chart(ctx, {
            type: 'bar',
            data: {
                labels: Object.keys(data),
                datasets: [{
                    label: 'Bookings',
                    data: Object.values(data),
                    backgroundColor: '#2563ff',
                    borderRadius: 8,
                    maxBarThickness: 46
                }]
            },
            options: {
                responsive: true,
                plugins: { legend: { display: false } },
                scales: {
                    x: { grid: { display: false }, ticks: { color: '#9aa4bd' } },
                    y: { beginAtZero: true, grid: { color: 'rgba(255,255,255,0.06)' }, ticks: { color: '#9aa4bd' } }
                }
            }
        });
    } catch (e) { console.error('Failed to load type chart', e); }
}
