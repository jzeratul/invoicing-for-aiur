import './App.css';
import React, {useEffect, useState} from "react"
import axios from "axios";
import Form from 'react-bootstrap/Form'
import "react-datepicker/dist/react-datepicker.css";
import {Alert, Button, Card, Col, Container, Row} from "react-bootstrap";
import DatePicker from "react-datepicker";
import {saveAs} from "file-saver";

const API_URL = '/api/v1';

function App() {

    const getSettings = () => {
        return axios.get(`${API_URL}/settings`)
    }

    const months = ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"]

    const [errorMessage, setErrorMessage] = useState()
    const [companies, setCompanies] = useState({})
    const [selectedCompany, setSelectedCompany] = useState({})
    const [companiesKeys, setCompaniesKeys] = useState([])
    const [percent, setPercent] = useState(0)
    const [startYear, setStartYear] = useState(0)
    const [startMonth, setStartMonth] = useState(0)

    const [fromDate, setFrom] = useState("")
    const [untilDate, setUntil] = useState("")
    const [expDate, setExp] = useState("")
    const [createdDate, setCreated] = useState("")
    const [amount, setAmount] = useState(0)
    const [income, setIncome] = useState(0)

    const [year1, setYear1] = useState(0)
    const [year2, setYear2] = useState(0)
    const [year3, setYear3] = useState(0)


    const [dataMonths, setDataMonths] = useState({})

    useEffect(() => {

        getSettings().then(
            (response) => {
                setCompanies(response.data.companiesSettings.companies)
                setCompaniesKeys(Object.keys(response.data.companiesSettings.companies))

                let currentDate = new Date()
                let start = new Date(currentDate.getFullYear(), currentDate.getMonth(), 1)
                let end = new Date(start.getFullYear(), start.getMonth() + 1, 0)
                let created = new Date(start.getFullYear(), start.getMonth() + 1, 0)
                let exp = new Date(start.getFullYear(), start.getMonth() + 1, 14)

                setFrom(start)
                setUntil(end)
                setExp(exp)
                setCreated(created)

                setStartYear(start.getFullYear())
                setYear1(start.getFullYear())
                setYear2(start.getFullYear() - 1)
                setYear3(start.getFullYear() - 2)
                setStartMonth(new Date().getMonth())
            },
            (error) => {
                setErrorMessage(error.response.data)
            })

        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [])

    const onChangeCompany = (company) => {
        setPercent(companies[company].percentage)
        setSelectedCompany(company)
    }
    const onChangePercent = (percent) => {
        setPercent(percent)
    }

    const onChangeStartDate = (startDate) => {
        setFrom(startDate)
    }

    const onChangeEndDate = (endDate) => {
        setUntil(endDate)
    }
    const onChangeExpirationDAte = (expirationDate) => {
        setExp(expirationDate)
    }
    const onChangeCreatedDate = (createdDate) => {
        setCreated(createdDate)
    }

    const onChangeAmount = (event) => {

        if (isValid(event)) {
            setAmount(event.target.value)
        }
    }
    const onChangeIncome = (event) => {
        if (isValid(event)) {
            setIncome(event.target.value)
        }
    }

    function isValid(evnt) {
        var charC = (evnt.which) ? evnt.which : evnt.keyCode;
        if (charC == 46) {
            if (amount.indexOf('.') === -1) {
                return true;
            } else {
                return false;
            }
        } else {
            if (charC > 31 && (charC < 48 || charC > 57))
                return false;
        }
        return true;
    }


    const onChangeMonth = (month) => {

        let m = months.indexOf(month)
        setStartMonth(m)

        let start = new Date(startYear, m, 1)
        let end = new Date(start.getFullYear(), m + 1, 0)
        let created = new Date(start.getFullYear(), m + 1, 0)

        let exp = new Date(start.getFullYear(), m + 1, 14)

        setFrom(start)
        setUntil(end)
        setExp(exp)
        setCreated(created)

    }

    const onChangeYear = (year) => {

        let start = new Date(year, startMonth, 1)
        let end = new Date(year, startMonth + 1, 0)
        let created = new Date(year, startMonth + 1, 0)

        let exp = new Date(year, startMonth + 1, 14)

        setStartYear(year)
        setFrom(start)
        setUntil(end)
        setExp(exp)
        setCreated(created)
    }

    const formatDate = (date) => {
        let y = date.getFullYear()
        let m = date.getMonth() + 1
        let d = date.getDate()

        let mm = m < 10 ? "0" + m : m
        let dd = d < 10 ? "0" + d : d

        return y + "-" + mm + "-" + dd
    }

    const generateReport = () => {

        let query = "invoiceFromDate=" + formatDate(fromDate) + "&invoiceToDate=" + formatDate(untilDate) + "&invoiceCompany=" + selectedCompany + "&invoiceAmount=" + amount + "&income=" + income + "&invoicePercent=" + percent + "&invoiceExpirationDate=" + formatDate(expDate) + "&invoiceCreatedDate=" + formatDate(createdDate)

        saveAs(`${API_URL}/invoice?` + query)
    }

    return (
        <>
            <Container fluid={true} className="mt-5">
                <Card className="card-default">
                    <Card.Header>
                        <h3 className="card-title">Invoice Generator</h3>
                    </Card.Header>
                    <Card.Body>
                        {errorMessage && (<Alert variant="danger">{errorMessage}</Alert>)}

                        <Row className="bg-light mt-3">
                            <Col xs={12} sm={8} md={6} lg={6} xl={4}>
                                <Form.Group className="mb-3">
                                    <Form.Label>Select the company</Form.Label>
                                    <Form.Control className="form-control-lg" as="select"
                                                  onChange={(e) => onChangeCompany(e.target.value)}>
                                        <option>Select the company</option>
                                        {companiesKeys.map(function (key, rowIdx) {
                                            return (
                                                <option key={rowIdx} value={key}>{companies[key].name}</option>
                                            )
                                        })}
                                    </Form.Control>
                                </Form.Group>

                            </Col>
                            <Col xs={12} sm={8} md={6} lg={6} xl={6}>
                                <Form.Group className="mb-3">
                                    <Form.Label>Percent</Form.Label>
                                    <Form.Control className="form-control-lg"
                                                  onChange={(e) => onChangePercent(e.target.value)}
                                                  value={percent}/>
                                </Form.Group>
                            </Col>
                        </Row>
                        <Row className="bg-light mt-3">

                            <Col xs={12} sm={8} md={6} lg={6} xl={6}>
                                <Form.Group className="mb-3">
                                    <Form.Label>Add Omzet <strong>if no income</strong> <small>Use . instead of , for decimals</small></Form.Label>
                                    <Form.Control onChange={(e) => onChangeAmount(e)}
                                                  value={amount} className="form-control form-control-lg"/>
                                </Form.Group>
                            </Col>

                            <Col xs={12} sm={8} md={6} lg={6} xl={6}>
                                <Form.Group className="mb-3">
                                    <Form.Label>Add Income if <strong>no omzet</strong> <small>Use . instead of , for decimals</small></Form.Label>
                                    <Form.Control onChange={(e) => onChangeIncome(e)}
                                                  value={income} className="form-control form-control-lg"/>
                                </Form.Group>
                            </Col>
                        </Row>
                        <Row className="bg-light mt-3">
                            <Col xs={12} sm={8} md={6} lg={6} xl={6}>
                                <Form.Group className="mb-3">
                                    <Form.Label>Select the year</Form.Label>
                                    <Form.Control className="form-control-lg" as="select"
                                                  onChange={(e) => onChangeYear(e.target.value)}
                                                  value={startYear}
                                    >
                                        <option value={year1}>{year1}</option>
                                        <option value={year2}>{year2}</option>
                                        <option value={year3}>{year3}</option>
                                    </Form.Control>
                                </Form.Group>

                            </Col>
                            <Col xs={12} sm={8} md={6} lg={6} xl={6}>
                                <Form.Group className="mb-3">
                                    <Form.Label>Select the month</Form.Label>
                                    <Form.Control className="form-control-lg" as="select"
                                                  onChange={(e) => onChangeMonth(e.target.value)}
                                    >
                                        <option>month</option>

                                        {months.map(function (month, rowIdx) {
                                            return (
                                                <option key={rowIdx} value={month}>{month}</option>
                                            )
                                        })}
                                    </Form.Control>
                                </Form.Group>

                            </Col>
                        </Row>
                        <Row className="bg-light mt-3">
                            <Col xs={12} sm={8} md={6} lg={6} xl={6}>
                                <Form.Group className="mb-3">
                                    <Form.Label>From</Form.Label>
                                    <div>
                                        <DatePicker selected={fromDate}
                                                    dateFormat="d MMM yyyy"
                                                    onChange={(date) => onChangeStartDate(date)}
                                                    className="form-control form-control-lg"/>
                                    </div>

                                </Form.Group>
                            </Col>
                            <Col xs={12} sm={8} md={6} lg={6} xl={6}>
                                <Form.Group className="mb-3">
                                    <Form.Label>To</Form.Label>
                                    <div>
                                        <DatePicker selected={untilDate}
                                                    dateFormat="d MMM yyyy"
                                                    onChange={(date) => onChangeEndDate(date)}
                                                    className="form-control form-control-lg"/>
                                    </div>
                                </Form.Group>
                            </Col>
                        </Row>
                        <Row className="bg-light mt-3">

                            <Col xs={12} sm={8} md={6} lg={6} xl={6}>
                                <Form.Group className="mb-3">
                                    <Form.Label>Expires</Form.Label>
                                    <div>
                                        <DatePicker selected={expDate}
                                                    dateFormat="d MMM yyyy"
                                                    onChange={(date) => onChangeExpirationDAte(date)}
                                                    className="form-control form-control-lg"/>
                                    </div>
                                </Form.Group>
                            </Col>
                            <Col xs={12} sm={8} md={6} lg={6} xl={6} className="mt-3">
                                <Form.Group className="mb-3">
                                    <Form.Label>Created on</Form.Label>
                                    <div>
                                        <DatePicker selected={createdDate}
                                                    dateFormat="d MMM yyyy"
                                                    onChange={(date) => onChangeCreatedDate(date)}
                                                    className="form-control form-control-lg"/>
                                    </div>
                                </Form.Group>
                            </Col>
                        </Row>
                        <Row className="bg-light mt-3">
                            <Col xs={12} sm={8} md={5} lg={4} xl={3} className="mt-3">
                                <Button variant={"info"} onClick={() => generateReport()}>Download</Button>
                            </Col>
                        </Row>
                    </Card.Body>
                </Card>
            </Container>

        </>
    );
}

export default App;
