import fetch from 'unfetch';

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
  fetch('/api/workers').then(checkStatus).then(response => response.json());

export const createWorker = worker =>
  fetch('/api/workers', {
    method: 'POST',
    headers: jsonHeaders,
    body: JSON.stringify(worker)
  }).then(checkStatus).then(response => response.json());

export const getSites = () =>
  fetch('/api/sites').then(checkStatus).then(response => response.json());

export const createSite = site =>
  fetch('/api/sites', {
    method: 'POST',
    headers: jsonHeaders,
    body: JSON.stringify(site)
  }).then(checkStatus).then(response => response.json());

export const clockIn = payload =>
  fetch('/api/attendance/clock-in', {
    method: 'POST',
    headers: jsonHeaders,
    body: JSON.stringify(payload)
  }).then(checkStatus).then(response => response.json());

export const clockOut = payload =>
  fetch('/api/attendance/clock-out', {
    method: 'POST',
    headers: jsonHeaders,
    body: JSON.stringify(payload)
  }).then(checkStatus).then(response => response.json());

export const getActiveAttendance = () =>
  fetch('/api/attendance/active').then(checkStatus).then(response => response.json());

export const getAttendanceLog = ({workerId, from, to, page = 0, size = 10}) =>
  fetch(`/api/attendance/log?workerId=${workerId}&from=${from}&to=${to}&page=${page}&size=${size}`)
    .then(checkStatus)
    .then(response => response.json());
