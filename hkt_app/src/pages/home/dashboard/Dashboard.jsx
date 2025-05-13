// Dashboard.jsx – realtime simulation with 10‑minute history seed
import SockJS from 'sockjs-client';
import Stomp from 'stompjs';

import { useEffect, useRef, useState } from 'react';
import { LineChart, Line, XAxis, YAxis, Tooltip, Legend, ResponsiveContainer } from 'recharts';
import { format, subMinutes } from 'date-fns';
import { BASE_URL_SOCKET, topicPath } from '../../../config/apiPath';
import { getToken } from '../../../utils/storage';
import { getVolume } from '../../../services/dashboard/dashboard';
import useNotify from '../../../hooks/useNotify';

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
    const { successRate, errorRate, totalRequests } = JSON.parse(payload.body).data;
    const now = new Date();
    const label = now.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit', second: '2-digit' });

    const newPoint = { time: label, successRate, errorRate, totalRequests };

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
  const [filteredData, setData] = useState([]);
  const notify = useNotify();
  // time range filter: default from 10 minutes ago to now
  const [selectedDate, setSelectedDate] = useState(format(new Date(), 'yyyy-MM-dd'));
  const [fromTime, setFromTime] = useState(() => format(subMinutes(new Date(), 10), 'HH:mm'));
  const [toTime, setToTime] = useState(() => format(new Date(), 'HH:mm'));

  const prevRate = useRef(filteredData[filteredData?.length - 1]?.successRate || 70);
  const prevTotal = useRef(filteredData[filteredData?.length - 1]?.totalRequests);

  useEffect(() => {
    getHisVolume({ selectedDate: selectedDate, fromTime: fromTime, toTime: toTime });
    // eslint-disable-next-line
  }, []);

  const getHisVolume = (params) => {
    getVolume(
      params,
      (res) => {
        console.log(res.data);
        let json = [];
        if (res.data) {
          json = res.data.list;
          const seed = json
            .slice(-MAX_POINTS)
            .map((pt) => ({ ...pt, timestamp: pt.timestamp || new Date(pt.time).toISOString() }));
          setData(seed);
          const last = seed.at(-1);
          prevRate.current = last?.successRate;
          prevTotal.current = last?.totalRequests;
        }
      },
      (err) => {
        notify.error(err?.data?.msg);
      }
    );
  };

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
    window.location.reload();
  };

  const TooltipBox = ({ active, payload, label }) => {
    if (active && payload && payload.length) {
      const { successRate, errorRate, totalRequests } = payload[0].payload;
      return (
        <div style={{ background: '#fff', border: '1px solid #ccc', padding: 8, fontSize: 12 }}>
          <p style={{ margin: 0, fontWeight: 600 }}>{label}</p>
          <p style={{ margin: 0 }}>Success Rate: {successRate}%</p>
          <p style={{ margin: 0 }}>Error Rate: {errorRate}%</p>
          <p style={{ margin: 0 }}>Total Req: {totalRequests}</p>
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
          <input type='date' value={selectedDate} onChange={(e) => setSelectedDate(e.target.value)} />
          <input type='time' value={fromTime} onChange={(e) => setFromTime(e.target.value)} />
          <input type='time' value={toTime} onChange={(e) => setToTime(e.target.value)} />
          <button onClick={reset} style={{ padding: '6px 12px', background: '#ccc', border: 'none', borderRadius: 4 }}>
            Reset
          </button>
        </div>
      </div>

      <div style={{ display: 'flex', flexDirection: 'row', gap: 16, marginTop: 32 }}>
        <div style={{ flex: 1, border: '1px solid #ddd', borderRadius: 8, padding: 16, background: 'rgb(36, 37, 37)' }}>
          <ResponsiveContainer width='100%' height={300}>
            <LineChart data={filteredData} syncId='metrics'>
              <XAxis dataKey='time' interval={59} tick={{ fontSize: 10, fill: '#e7e3e1' }} />
              <YAxis domain={[0, 100]} orientation='right' tickFormatter={(v) => `${v}%`} />
              <Tooltip content={<TooltipBox />} />
              <Legend verticalAlign='top' />
              <Line type='monotone' dataKey='successRate' stroke='#4CAF50' dot={false} name='Success Rate' />
              <Line type='monotone' dataKey='errorRate' stroke='#F44336' dot={false} name='Error Rate' />
            </LineChart>
          </ResponsiveContainer>
        </div>
        <div style={{ flex: 1, border: '1px solid #ddd', borderRadius: 8, padding: 16, background: 'rgb(36, 37, 37)' }}>
          <ResponsiveContainer width='100%' height={300}>
            <LineChart data={filteredData} syncId='metrics'>
              <XAxis dataKey='time' interval={59} tick={{ fontSize: 10, fill: '#e7e3e1' }} />
              <YAxis domain={['auto', 'auto']} tick={{ fontSize: 10, fill: '#3c81ef' }} />
              <Tooltip content={<TooltipBox />} />
              <Legend verticalAlign='top' />
              <Line type='monotone' dataKey='totalRequests' stroke='#2196F3' dot={false} name='Total Requests' />
            </LineChart>
          </ResponsiveContainer>
        </div>
      </div>
    </div>
  );
}
