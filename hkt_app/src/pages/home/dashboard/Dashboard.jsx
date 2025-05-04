// Dashboard.jsx – realtime simulation with 10‑minute history seed
import SockJS from 'sockjs-client';
import Stomp from 'stompjs';

import { useEffect, useRef, useState } from 'react';
import { LineChart, Line, XAxis, YAxis, Tooltip, Legend, ResponsiveContainer } from 'recharts';
import { format, subSeconds } from 'date-fns';
import { BASE_URL_SOCKET, topicPath } from '../../../config/apiPath';
import { getToken } from '../../../utils/storage';

const MAX_POINTS = 600; // 10 minutes @ 1s interval
let stompClient;
export default function Dashboard() {
  const [isError, setIsError] = useState(true);
  const token = getToken();

  useEffect(() => {
    if (isError && token) {
      connectServerGetData();
    }
    // eslint-disable-next-line
  }, [isError, token]);
  const connectServerGetData = () => {
    setIsError(false);
    const socket = new SockJS(BASE_URL_SOCKET);
    stompClient = Stomp.over(socket);
    stompClient.debug = function (str) {
      // append the debug log to a #debug div
      console.log(str);
    };
    stompClient.heartbeat = {
      outgoing: 0,
      incoming: 10000
    };
    stompClient.connect({ Authorization: 'Bearer ' + token }, onConnected, onError);
  };
  const onConnected = () => {
    stompClient.subscribe(topicPath.topicVolume, onMessageReceived);
  };

  const onMessageReceived = (payload) => {
    const { successRate, errorRate } = JSON.parse(payload.body).data;
    const now = new Date();
    const label = now.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit', second: '2-digit' });

    const newPoint = { time: label, successRate, errorRate };

    setData((prev) => {
      const arr = [...prev, newPoint];
      return arr.length > MAX_POINTS ? arr.slice(-MAX_POINTS) : arr;
    });
  };

  const onError = (error) => {
    console.log(error);
    setTimeout(() => {
      setIsError(true);
    }, 5000);
  };
  const [data, setData] = useState(() => generateSeed());
  const [status, setStatus] = useState('UP');
  const [selectedDate, setSelectedDate] = useState(format(new Date(), 'yyyy-MM-dd'));
  const prevRate = useRef(data[data.length - 1]?.successRate || 70);

  /* ---------- simulate realtime data ---------- */
  /*useEffect(() => {
    const id = setInterval(() => {
      const now = new Date();
      const label = now.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit', second: '2-digit' });

      let next = prevRate.current + Math.floor(Math.random() * 5 - 2);
      next = Math.max(5, Math.min(100, next));
      prevRate.current = next;
      const err = 100 - next;
      const point = { time: label, successRate: next, errorRate: err };
      // console.log(point);
      setData((prev) => {
        const arr = [...prev, point];
        return arr.length > MAX_POINTS ? arr.slice(-MAX_POINTS) : arr;
      });

      setStatus(err > 30 ? 'DOWN' : 'UP');
    }, 1000);
    return () => clearInterval(id);
  }, []);*/

  /* ---------- helpers ---------- */
  const reset = () => {
    const seed = generateSeed();
    setData(seed);
    prevRate.current = seed[seed.length - 1].successRate;
    setStatus('UP');
  };

  const exportCSV = () => {
    const rows = [['Time', 'SuccessRate(%)', 'ErrorRate(%)'], ...data.map((r) => [r.time, r.successRate, r.errorRate])];
    const csv = rows.map((row) => row.join(',')).join('\n');
    const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
    const a = document.createElement('a');
    a.href = URL.createObjectURL(blob);
    a.download = `request_rate_${selectedDate}.csv`;
    a.click();
  };

  const TooltipBox = ({ active, payload, label }) => {
    if (active && payload && payload.length) {
      const { successRate, errorRate } = payload[0].payload;
      return (
        <div style={{ background: '#fff', border: '1px solid #ccc', padding: 8, fontSize: 12 }}>
          <p style={{ margin: 0, fontWeight: 600 }}>{label}</p>
          <p style={{ margin: 0 }}>Success Rate: {successRate}%</p>
          <p style={{ margin: 0 }}>Error Rate: {errorRate}%</p>
        </div>
      );
    }
    return null;
  };

  /* ---------- UI ---------- */
  return (
    <div style={{ padding: 24, fontFamily: 'sans-serif' }}>
      <div style={{ display: 'flex', flexWrap: 'wrap', justifyContent: 'space-between', gap: 16 }}>
        <h1 style={{ fontSize: 24, fontWeight: 'bold', color: '#fff' }}>Realtime Request Rates (Simulated)</h1>
        <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
          <span style={{ fontWeight: 600, color: status === 'UP' ? 'green' : 'red' }}>{status}</span>
          <input
            type='date'
            value={selectedDate}
            onChange={(e) => setSelectedDate(e.target.value)}
            style={{ padding: '4px 8px', border: '1px solid #ccc', borderRadius: 4 }}
          />
          <button
            onClick={exportCSV}
            style={{ padding: '6px 12px', background: '#4CAF50', color: '#fff', border: 'none', borderRadius: 4 }}
          >
            Export CSV
          </button>
          <button onClick={reset} style={{ padding: '6px 12px', background: '#ccc', border: 'none', borderRadius: 4 }}>
            Reset
          </button>
        </div>
      </div>

      <div style={{ marginTop: 32, border: '1px solid #ddd', borderRadius: 8, padding: 16, background: '#fafafa' }}>
        <ResponsiveContainer width='100%' height={300}>
          <LineChart data={data} syncId='rates'>
            <XAxis dataKey='time' tick={{ fontSize: 10 }} interval={59} minTickGap={15} />
            <YAxis domain={[0, 100]} orientation='right' tickFormatter={(v) => `${v}%`} />
            <Tooltip content={<TooltipBox />} />
            <Legend verticalAlign='top' />
            <Line
              type='monotone'
              dataKey='successRate'
              stroke='#4CAF50'
              dot={false}
              name='Success Rate'
              animationDuration={300}
            />
            <Line
              type='monotone'
              dataKey='errorRate'
              stroke='#F44336'
              dot={false}
              name='Error Rate'
              animationDuration={300}
            />
          </LineChart>
        </ResponsiveContainer>
      </div>
    </div>
  );
}

/* ---------- util: generate 10‑minute seed ---------- */
function generateSeed() {
  const seed = [];
  let rate = 70;
  for (let i = MAX_POINTS - 1; i >= 0; i--) {
    const t = subSeconds(new Date(), i);
    const label = t.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit', second: '2-digit' });
    rate += Math.floor(Math.random() * 5 - 2);
    rate = Math.max(5, Math.min(100, rate));
    seed.push({ time: label, successRate: rate, errorRate: 100 - rate });
  }
  return seed;
}
