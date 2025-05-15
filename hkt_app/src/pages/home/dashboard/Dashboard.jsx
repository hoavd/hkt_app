// Dashboard.jsx – realtime simulation with 10‑minute history seed
import SockJS from 'sockjs-client';
import Stomp from 'stompjs';

import { useEffect, useRef, useState } from 'react';
import { AreaChart, Area, XAxis, YAxis, Tooltip, Legend, ResponsiveContainer, BarChart, Bar } from 'recharts';
import { parse, format, subMinutes } from 'date-fns';
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
    const { successRate, errorRate, totalRequests, timestamp } = JSON.parse(payload.body).data;

    // Chuyển 'YYYY-MM-DD HH:mm:ss' về Date chính xác
    const date = parse(timestamp, 'yyyy-MM-dd HH:mm:ss', new Date());

    const label = date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit', second: '2-digit' });

    const newPoint = { time: label, successRate, errorRate, totalRequests, timestamp: date.toISOString() };

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
        let json = [];
        if (res.data) {
          json = res.data.list;
          const seed = json.slice(-MAX_POINTS).map((pt) => {
            const date = parse(pt.timestamp, 'yyyy-MM-dd HH:mm:ss', new Date());
            return {
              ...pt,
              time: date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit', second: '2-digit' }),
              timestamp: date.toISOString()
            };
          });
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

  /* ---------- helpers ---------- */
  const reset = () => {
    window.location.reload();
  };

  const TooltipBox = ({ active, payload }) => {
    if (active && payload && payload.length) {
      const { successRate, errorRate, totalRequests, timestamp } = payload[0].payload;

      // Chuyển đổi timestamp sang giờ Việt Nam (hoặc local time)
      const localDate = new Date(timestamp);
      const formattedTime = localDate.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit', second: '2-digit' });

      return (
        <div style={{ background: '#fff', border: '1px solid #ccc', padding: 8, fontSize: 12 }}>
          <p style={{ margin: 0, fontWeight: 600 }}>{formattedTime}</p>
          <p style={{ margin: 0 }}>Success Rate: {successRate}%</p>
          <p style={{ margin: 0 }}>Error Rate: {errorRate}%</p>
          <p style={{ margin: 0 }}>Total Req: {totalRequests}</p>
        </div>
      );
    }
    return null;
  };

  const latestSuccessRate = filteredData.length > 0 ? filteredData[filteredData.length - 1].successRate : 0;

  // Hàm để lấy màu theo tỷ lệ
  const getSuccessColor = (rate) => {
    if (rate > 97) return 'green';
    if (rate > 95 && rate <= 97) return 'yellow';
    if (rate <= 95) return 'red';
    return 'inherit';
  };
  const generateFixedBars = (data, size = 20) => {
    // Nếu data đã đủ size thì trả luôn
    if (data.length >= size) return data.slice(-size);

    const paddedData = [...data];

    // Lấy timestamp gần nhất hoặc giờ hiện tại làm ref
    const lastTimestamp = data.length > 0 ? new Date(data[data.length - 1].timestamp) : new Date();

    // Tạo các cột trống ở đầu để đủ 20 cột
    for (let i = data.length; i < size; i++) {
      const time = new Date(lastTimestamp.getTime() - 1000 * (size - i)); // giả sử mỗi cột cách nhau 1 giây
      paddedData.unshift({
        timestamp: time.toISOString(),
        totalRequests: 0,
        successRate: 0,
        errorRate: 0,
        time: time.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit', second: '2-digit' })
      });
    }

    return paddedData;
  };

  const barData = generateFixedBars(filteredData, 20);
  const formatTime = (timestamp) => {
    return new Date(timestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit', second: '2-digit' });
  };
  /* ---------- UI ---------- */
  return (
    <div style={{ padding: 24, fontFamily: 'sans-serif' }}>
      <div style={{ display: 'flex', flexWrap: 'wrap', justifyContent: 'space-between', gap: 16 }}>
        <h1 style={{ fontSize: 24, fontWeight: 'bold', color: '#fff' }}>Realtime Request Rates (Simulated)</h1>
        <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
          <div style={{ display: 'none' }}>
            <input type='date' value={selectedDate} onChange={(e) => setSelectedDate(e.target.value)} />
            <input type='time' value={fromTime} onChange={(e) => setFromTime(e.target.value)} />
            <input type='time' value={toTime} onChange={(e) => setToTime(e.target.value)} />
          </div>
          <div
            style={{
              fontWeight: 'bold',
              fontSize: 20,
              color: getSuccessColor(latestSuccessRate)
            }}
          >
            {latestSuccessRate}%
          </div>
          <button onClick={reset} style={{ padding: '6px 12px', background: '#ccc', border: 'none', borderRadius: 4 }}>
            Reset
          </button>
        </div>
      </div>

      <div style={{ display: 'flex', flexDirection: 'row', gap: 16, marginTop: 32 }}>
        <div style={{ flex: 1, border: '1px solid #ddd', borderRadius: 8, padding: 16, background: 'rgb(36, 37, 37)' }}>
          <ResponsiveContainer width='100%' height={300}>
            <AreaChart data={filteredData}>
              {/*<XAxis dataKey='time' interval={59} tick={{ fontSize: 10, fill: '#e7e3e1' }} />*/}
              <XAxis dataKey='timestamp' tickFormatter={formatTime} tick={{ fontSize: 10, fill: '#e7e3e1' }} />
              <YAxis domain={[0, 100]} orientation='right' tickFormatter={(v) => `${v}%`} />
              <Tooltip content={<TooltipBox />} />
              <Legend verticalAlign='top' />
              <Area
                type='monotone'
                dataKey='successRate'
                stroke='#4CAF50'
                fill='#4CAF50'
                fillOpacity={0.3}
                name='Success Rate'
              />
            </AreaChart>
          </ResponsiveContainer>
        </div>
        <div style={{ flex: 1, border: '1px solid #ddd', borderRadius: 8, padding: 16, background: 'rgb(36, 37, 37)' }}>
          <ResponsiveContainer width='100%' height={300}>
            <BarChart data={barData}>
              <XAxis dataKey='timestamp' tickFormatter={formatTime} tick={{ fontSize: 10, fill: '#e7e3e1' }} />
              <YAxis domain={[0, 20]} tick={{ fontSize: 10, fill: '#3c81ef' }} />
              <Tooltip content={<TooltipBox />} />
              <Legend verticalAlign='top' />
              <Bar dataKey='totalRequests' fill='#2196F3' name='Total Requests' />
            </BarChart>
          </ResponsiveContainer>
        </div>
      </div>
    </div>
  );
}
