import {useCallback, useEffect, useMemo, useState} from 'react';
import {
  Alert,
  Badge,
  Button,
  Col,
  DatePicker,
  Form,
  Input,
  InputNumber,
  Layout,
  PageHeader,
  Row,
  Select,
  Space,
  Spin,
  Statistic,
  Switch,
  Table,
  Tabs,
  Tag
} from 'antd';
import {
  ClockCircleOutlined,
  EditOutlined,
  LoginOutlined,
  LogoutOutlined,
  ReloadOutlined,
  TeamOutlined,
  EnvironmentOutlined
} from '@ant-design/icons';
import moment from 'moment';
import {
  clockIn,
  clockOut,
  createSite,
  createWorker,
  getActiveAttendance,
  getAttendanceLog,
  getOvertimeSummary,
  getSites,
  getWorkers,
  settleOvertime,
  updateWorker
} from './client';
import {errorNotification, successNotification} from './Notification';
import './App.css';

const {Content} = Layout;
const {Option} = Select;
const {TabPane} = Tabs;
const designations = ['MASON', 'ELECTRICIAN', 'PLUMBER', 'SUPERVISOR', 'HELPER'];

const today = moment();
const monthStart = moment().startOf('month');

function App() {
  const [workers, setWorkers] = useState([]);
  const [sites, setSites] = useState([]);
  const [activeAttendance, setActiveAttendance] = useState([]);
  const [attendanceLog, setAttendanceLog] = useState([]);
  const [attendanceTotal, setAttendanceTotal] = useState(0);
  const [editingWorkerId, setEditingWorkerId] = useState(null);
  const [overtimeSummary, setOvertimeSummary] = useState(null);
  const [overtimeQuery, setOvertimeQuery] = useState(null);
  const [loading, setLoading] = useState(false);
  const [workerForm] = Form.useForm();
  const [siteForm] = Form.useForm();
  const [clockInForm] = Form.useForm();
  const [clockOutForm] = Form.useForm();
  const [logForm] = Form.useForm();
  const [overtimeForm] = Form.useForm();

  const activeWorkers = useMemo(
    () => workers.filter(worker => worker.active),
    [workers]
  );

  const activeSites = useMemo(
    () => sites.filter(site => site.active),
    [sites]
  );

  const handleError = useCallback(async (error) => {
    if (error.response) {
      const payload = await error.response.json();
      errorNotification(payload.error || 'Request failed', payload.message || 'Please try again');
      return;
    }
    errorNotification('Request failed', error.message);
  }, []);

  const loadDashboard = useCallback(() => {
    setLoading(true);
    Promise.all([getWorkers(), getSites(), getActiveAttendance()])
      .then(([workerData, siteData, activeData]) => {
        setWorkers(workerData);
        setSites(siteData);
        setActiveAttendance(activeData);
      })
      .catch(handleError)
      .finally(() => setLoading(false));
  }, [handleError]);

  useEffect(() => {
    loadDashboard();
    logForm.setFieldsValue({
      range: [monthStart, today]
    });
    overtimeForm.setFieldsValue({
      month: moment().subtract(1, 'month')
    });
  }, [loadDashboard, logForm, overtimeForm]);

  const submitWorker = values => {
    const action = editingWorkerId ? updateWorker(editingWorkerId, values) : createWorker(values);
    action
      .then(worker => {
        successNotification(editingWorkerId ? 'Worker updated' : 'Worker created', `${worker.name} is ready for attendance`);
        setEditingWorkerId(null);
        workerForm.resetFields();
        loadDashboard();
      })
      .catch(handleError);
  };

  const editWorker = worker => {
    setEditingWorkerId(worker.id);
    workerForm.setFieldsValue({
      name: worker.name,
      phone: worker.phone,
      designation: worker.designation,
      dailyWageRate: worker.dailyWageRate,
      active: worker.active
    });
  };

  const cancelWorkerEdit = () => {
    setEditingWorkerId(null);
    workerForm.resetFields();
  };

  const submitSite = values => {
    createSite(values)
      .then(site => {
        successNotification('Site created', site.name);
        siteForm.resetFields();
        loadDashboard();
      })
      .catch(handleError);
  };

  const submitClockIn = values => {
    clockIn(values)
      .then(result => {
        successNotification('Clock-in recorded', `${result.workerName} at ${result.siteName}`);
        clockInForm.resetFields();
        loadDashboard();
      })
      .catch(handleError);
  };

  const submitClockOut = values => {
    clockOut(values)
      .then(result => {
        successNotification('Clock-out recorded', `${result.workerName}: ${result.totalHoursWorked || 0} hours`);
        clockOutForm.resetFields();
        loadDashboard();
      })
      .catch(handleError);
  };

  const submitLogSearch = values => {
    const [from, to] = values.range;
    getAttendanceLog({
      workerId: values.workerId,
      from: from.format('YYYY-MM-DD'),
      to: to.format('YYYY-MM-DD')
    })
      .then(page => {
        setAttendanceLog(page.content || []);
        setAttendanceTotal(page.totalElements || 0);
      })
      .catch(handleError);
  };

  const submitOvertimeSearch = values => {
    const query = {
      workerId: values.workerId,
      month: values.month.format('YYYY-MM')
    };
    setOvertimeQuery(query);
    getOvertimeSummary(query)
      .then(setOvertimeSummary)
      .catch(handleError);
  };

  const submitOvertimeSettlement = () => {
    if (!overtimeQuery) {
      errorNotification('Missing overtime search', 'Search a worker and month before settlement');
      return;
    }

    settleOvertime(overtimeQuery)
      .then(result => {
        successNotification('Overtime settled', `Total amount: ₹${result.totalAmount}`);
        return getOvertimeSummary(overtimeQuery);
      })
      .then(setOvertimeSummary)
      .catch(handleError);
  };

  const workerColumns = [
    {title: 'ID', dataIndex: 'id', width: 70},
    {title: 'Name', dataIndex: 'name'},
    {title: 'Phone', dataIndex: 'phone'},
    {title: 'Designation', dataIndex: 'designation', render: value => <Tag>{value}</Tag>},
    {title: 'Daily Wage', dataIndex: 'dailyWageRate', render: value => `₹${value}`},
    {title: 'Status', dataIndex: 'active', render: active => active ? <Badge status="success" text="Active"/> : <Badge status="default" text="Inactive"/>},
    {title: 'Action', render: (_, worker) => <Button icon={<EditOutlined/>} onClick={() => editWorker(worker)}>Edit</Button>}
  ];

  const siteColumns = [
    {title: 'ID', dataIndex: 'id', width: 70},
    {title: 'Site', dataIndex: 'name'},
    {title: 'Location', dataIndex: 'location'},
    {title: 'Status', dataIndex: 'active', render: active => active ? <Badge status="success" text="Active"/> : <Badge status="default" text="Inactive"/>}
  ];

  const activeColumns = [
    {title: 'Worker', dataIndex: 'workerName'},
    {title: 'Designation', dataIndex: 'designation', render: value => <Tag color="blue">{value}</Tag>},
    {title: 'Site', dataIndex: 'siteName'},
    {title: 'Location', dataIndex: 'siteLocation'},
    {title: 'Clock In', dataIndex: 'clockInAt', render: value => moment(value).format('DD MMM YYYY, hh:mm A')}
  ];

  const logColumns = [
    {title: 'Worker', dataIndex: 'workerName'},
    {title: 'Site', dataIndex: 'siteName'},
    {title: 'Clock In', dataIndex: 'clockInAt', render: value => moment(value).format('DD MMM YYYY, hh:mm A')},
    {title: 'Clock Out', dataIndex: 'clockOutAt', render: value => value ? moment(value).format('DD MMM YYYY, hh:mm A') : <Tag color="gold">Open</Tag>},
    {title: 'Hours', dataIndex: 'totalHoursWorked', render: value => value || '-'},
    {title: 'OT', dataIndex: 'overtimeHours', render: value => value || '-'},
    {title: 'Flagged', dataIndex: 'flagged', render: value => value ? <Tag color="red">Review</Tag> : <Tag>Clear</Tag>}
  ];

  const overtimeColumns = [
    {title: 'Date', dataIndex: 'date'},
    {title: 'Hours', dataIndex: 'overtimeHours'},
    {title: 'Rate', dataIndex: 'overtimeRateApplied', render: value => `₹${value}`},
    {title: 'Amount', dataIndex: 'amount', render: value => `₹${value}`},
    {title: 'Status', dataIndex: 'settlementStatus', render: value => value === 'SETTLED' ? <Tag color="green">SETTLED</Tag> : <Tag color="gold">PENDING</Tag>}
  ];

  return (
    <Layout className="workforce-shell">
      <Content className="workforce-content">
        <PageHeader
          className="workforce-header"
          title="Workforce Attendance"
          subTitle="Daily site attendance, active headcount, and shift history"
          extra={[
            <Button key="refresh" icon={<ReloadOutlined/>} onClick={loadDashboard}>Refresh</Button>
          ]}
        />

        <Spin spinning={loading}>
          <Row gutter={[16, 16]} className="metrics-row">
            <Col xs={24} sm={8}>
              <Statistic title="Workers" value={workers.length} prefix={<TeamOutlined/>}/>
            </Col>
            <Col xs={24} sm={8}>
              <Statistic title="Sites" value={sites.length} prefix={<EnvironmentOutlined/>}/>
            </Col>
            <Col xs={24} sm={8}>
              <Statistic title="Currently On Site" value={activeAttendance.length} prefix={<ClockCircleOutlined/>}/>
            </Col>
          </Row>

          <Tabs defaultActiveKey="attendance" className="workforce-tabs">
            <TabPane tab="Attendance" key="attendance">
              <Row gutter={[16, 16]}>
                <Col xs={24} lg={12}>
                  <section className="panel">
                    <h2><LoginOutlined/> Clock In</h2>
                    <Form form={clockInForm} layout="vertical" onFinish={submitClockIn}>
                      <Form.Item name="workerId" label="Worker" rules={[{required: true}]}>
                        <Select placeholder="Select worker">
                          {activeWorkers.map(worker => <Option key={worker.id} value={worker.id}>{worker.name} - {worker.designation}</Option>)}
                        </Select>
                      </Form.Item>
                      <Form.Item name="siteId" label="Site" rules={[{required: true}]}>
                        <Select placeholder="Select site">
                          {activeSites.map(site => <Option key={site.id} value={site.id}>{site.name} - {site.location}</Option>)}
                        </Select>
                      </Form.Item>
                      <Button type="primary" htmlType="submit" icon={<LoginOutlined/>}>Clock In</Button>
                    </Form>
                  </section>
                </Col>
                <Col xs={24} lg={12}>
                  <section className="panel">
                    <h2><LogoutOutlined/> Clock Out</h2>
                    <Form form={clockOutForm} layout="vertical" onFinish={submitClockOut}>
                      <Form.Item name="workerId" label="Worker" rules={[{required: true}]}>
                        <Select placeholder="Select worker">
                          {workers.map(worker => <Option key={worker.id} value={worker.id}>{worker.name}</Option>)}
                        </Select>
                      </Form.Item>
                      <Button type="primary" htmlType="submit" icon={<LogoutOutlined/>}>Clock Out</Button>
                    </Form>
                  </section>
                </Col>
              </Row>

              <section className="panel table-panel">
                <Space className="panel-title">
                  <h2>Active Workers</h2>
                  <Tag color="green">Redis</Tag>
                </Space>
                <Table rowKey="workerId" dataSource={activeAttendance} columns={activeColumns} pagination={false}/>
              </section>
            </TabPane>

            <TabPane tab="Workers" key="workers">
              <Row gutter={[16, 16]}>
                <Col xs={24} lg={8}>
                  <section className="panel">
                    <h2>{editingWorkerId ? 'Update Worker' : 'Add Worker'}</h2>
                    <Form form={workerForm} layout="vertical" onFinish={submitWorker} initialValues={{active: true}}>
                      <Form.Item name="name" label="Name" rules={[{required: true}]}>
                        <Input/>
                      </Form.Item>
                      <Form.Item name="phone" label="Phone" rules={[{required: true}]}>
                        <Input/>
                      </Form.Item>
                      <Form.Item name="designation" label="Designation" rules={[{required: true}]}>
                        <Select>{designations.map(value => <Option key={value} value={value}>{value}</Option>)}</Select>
                      </Form.Item>
                      <Form.Item name="dailyWageRate" label="Daily Wage Rate" rules={[{required: true}]}>
                        <InputNumber min={0} precision={2} className="full-width"/>
                      </Form.Item>
                      <Form.Item name="active" label="Active" valuePropName="checked">
                        <Switch/>
                      </Form.Item>
                      <Space>
                        <Button type="primary" htmlType="submit">{editingWorkerId ? 'Update Worker' : 'Create Worker'}</Button>
                        {editingWorkerId && <Button onClick={cancelWorkerEdit}>Cancel</Button>}
                      </Space>
                    </Form>
                  </section>
                </Col>
                <Col xs={24} lg={16}>
                  <section className="panel table-panel">
                    <h2>Workers</h2>
                    <Table rowKey="id" dataSource={workers} columns={workerColumns} pagination={{pageSize: 8}}/>
                  </section>
                </Col>
              </Row>
            </TabPane>

            <TabPane tab="Sites" key="sites">
              <Row gutter={[16, 16]}>
                <Col xs={24} lg={8}>
                  <section className="panel">
                    <h2>Add Site</h2>
                    <Form form={siteForm} layout="vertical" onFinish={submitSite} initialValues={{active: true}}>
                      <Form.Item name="name" label="Site Name" rules={[{required: true}]}>
                        <Input/>
                      </Form.Item>
                      <Form.Item name="location" label="Location" rules={[{required: true}]}>
                        <Input/>
                      </Form.Item>
                      <Form.Item name="active" label="Active" valuePropName="checked">
                        <Switch/>
                      </Form.Item>
                      <Button type="primary" htmlType="submit">Create Site</Button>
                    </Form>
                  </section>
                </Col>
                <Col xs={24} lg={16}>
                  <section className="panel table-panel">
                    <h2>Sites</h2>
                    <Table rowKey="id" dataSource={sites} columns={siteColumns} pagination={{pageSize: 8}}/>
                  </section>
                </Col>
              </Row>
            </TabPane>

            <TabPane tab="History" key="history">
              <section className="panel">
                <h2>Attendance History</h2>
                <Form form={logForm} layout="inline" onFinish={submitLogSearch} className="history-form">
                  <Form.Item name="workerId" rules={[{required: true}]}>
                    <Select placeholder="Worker" className="worker-select">
                      {workers.map(worker => <Option key={worker.id} value={worker.id}>{worker.name}</Option>)}
                    </Select>
                  </Form.Item>
                  <Form.Item name="range" rules={[{required: true}]}>
                    <DatePicker.RangePicker/>
                  </Form.Item>
                  <Button type="primary" htmlType="submit">Search</Button>
                </Form>
                {attendanceTotal > 0 && <Alert className="history-alert" type="info" showIcon message={`${attendanceTotal} attendance record(s) found`}/>}
                <Table rowKey="id" dataSource={attendanceLog} columns={logColumns} pagination={{pageSize: 10}}/>
              </section>
            </TabPane>

            <TabPane tab="Overtime" key="overtime">
              <section className="panel">
                <h2>Monthly Overtime</h2>
                <Form form={overtimeForm} layout="inline" onFinish={submitOvertimeSearch} className="history-form">
                  <Form.Item name="workerId" rules={[{required: true}]}>
                    <Select placeholder="Worker" className="worker-select">
                      {workers.map(worker => <Option key={worker.id} value={worker.id}>{worker.name}</Option>)}
                    </Select>
                  </Form.Item>
                  <Form.Item name="month" rules={[{required: true}]}>
                    <DatePicker picker="month"/>
                  </Form.Item>
                  <Button type="primary" htmlType="submit">Search</Button>
                  <Button onClick={submitOvertimeSettlement} disabled={!overtimeSummary || overtimeSummary.settlementStatus === 'SETTLED'}>
                    Settle
                  </Button>
                </Form>

                {overtimeSummary && (
                  <Row gutter={[16, 16]} className="overtime-summary">
                    <Col xs={24} sm={8}>
                      <Statistic title="Overtime Hours" value={overtimeSummary.totalOvertimeHours}/>
                    </Col>
                    <Col xs={24} sm={8}>
                      <Statistic title="Payout Amount" value={overtimeSummary.totalPayoutAmount} prefix="₹"/>
                    </Col>
                    <Col xs={24} sm={8}>
                      <Statistic title="Settlement" value={overtimeSummary.settlementStatus}/>
                    </Col>
                  </Row>
                )}

                <Table
                  rowKey={record => `${record.date}-${record.amount}`}
                  dataSource={overtimeSummary ? overtimeSummary.breakdown : []}
                  columns={overtimeColumns}
                  pagination={false}
                />
              </section>
            </TabPane>
          </Tabs>
        </Spin>
      </Content>
    </Layout>
  );
}

export default App;
