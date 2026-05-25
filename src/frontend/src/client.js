import fetch from 'unfetch';

const API_BASE_URL = process.env.REACT_APP_API_BASE_URL || 'http://localhost:8082';

const checkStatus = response => {
  if (response.ok) {
    return response;
  }
  const error = new Error(response.statusText);
  error.response = response;
  return Promise.reject(error);
};

const jsonHeaders = {
  'Content-Type': 'application/json'
};

export const getWorkers = () =>
  fetch(`${API_BASE_URL}/api/workers`).then(checkStatus).then(response => response.json());

export const createWorker = worker =>
  fetch(`${API_BASE_URL}/api/workers`, {
    method: 'POST',
    headers: jsonHeaders,
    body: JSON.stringify(worker)
  }).then(checkStatus).then(response => response.json());

export const updateWorker = (workerId, worker) =>
  fetch(`${API_BASE_URL}/api/workers/${workerId}`, {
    method: 'PUT',
    headers: jsonHeaders,
    body: JSON.stringify(worker)
  }).then(checkStatus).then(response => response.json());

export const getSites = () =>
  fetch(`${API_BASE_URL}/api/sites`).then(checkStatus).then(response => response.json());

export const createSite = site =>
  fetch(`${API_BASE_URL}/api/sites`, {
    method: 'POST',
    headers: jsonHeaders,
    body: JSON.stringify(site)
  }).then(checkStatus).then(response => response.json());

export const clockIn = payload =>
  fetch(`${API_BASE_URL}/api/attendance/clock-in`, {
    method: 'POST',
    headers: jsonHeaders,
    body: JSON.stringify(payload)
  }).then(checkStatus).then(response => response.json());

export const clockOut = payload =>
  fetch(`${API_BASE_URL}/api/attendance/clock-out`, {
    method: 'POST',
    headers: jsonHeaders,
    body: JSON.stringify(payload)
  }).then(checkStatus).then(response => response.json());

export const getActiveAttendance = () =>
  fetch(`${API_BASE_URL}/api/attendance/active`).then(checkStatus).then(response => response.json());

export const getAttendanceLog = ({workerId, from, to, page = 0, size = 10}) =>
  fetch(`${API_BASE_URL}/api/attendance/log?workerId=${workerId}&from=${from}&to=${to}&page=${page}&size=${size}`)
    .then(checkStatus)
    .then(response => response.json());

export const getOvertimeSummary = ({workerId, month}) =>
  fetch(`${API_BASE_URL}/api/overtime/summary/${workerId}?month=${month}`)
    .then(checkStatus)
    .then(response => response.json());

export const settleOvertime = ({workerId, month}) =>
  fetch(`${API_BASE_URL}/api/overtime/settle/${workerId}?month=${month}`, {
    method: 'POST'
  }).then(checkStatus).then(response => response.json());
