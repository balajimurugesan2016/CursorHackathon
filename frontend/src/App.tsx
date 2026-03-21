import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { OperationalDashboard } from './pages/OperationalDashboard';
import { PredictionsSimulation } from './pages/PredictionsSimulation';
import { PlaceholderPage } from './pages/PlaceholderPage';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<OperationalDashboard />} />
        <Route path="/predictions" element={<PredictionsSimulation />} />
        <Route path="/plants" element={<PlaceholderPage title="PLANTS" />} />
        <Route path="/suppliers" element={<PlaceholderPage title="SUPPLIERS" />} />
        <Route path="/disruptions" element={<PlaceholderPage title="DISRUPTIONS" />} />
        <Route path="/settings" element={<PlaceholderPage title="SETTINGS" />} />
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
