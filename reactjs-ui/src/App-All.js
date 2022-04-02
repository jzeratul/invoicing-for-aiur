import './App.css';
import React, {useEffect, useState} from "react"
import axios from "axios";
import Form from 'react-bootstrap/Form'
import "react-datepicker/dist/react-datepicker.css";
import {Alert, Button, Card, Col, Container, Row} from "react-bootstrap";
import DatePicker from "react-datepicker";

const API_URL = '/api/v1';

function App() {

    const getInvoice = (query) => {
        return axios.get(`${API_URL}/invoice?` + query)
    }

    const getSettings = () => {
        return axios.get(`${API_URL}/settings`)
    }

    const months = ["jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "sep", "oct", "nov", "dec"]

    const [errorMessage, setErrorMessage] = useState()
    const [clinics, setClinics] = useState({})
    const [selectedClinic, setSelectedClinic] = useState({})
    const [clinicsKeys, setClinicsKeys] = useState([])
    const [percent, setPercent] = useState(0)
    const [startYear, setStartYear] = useState(0)
    const [startDate, setStartDate] = useState()

    const [invoiceStartDates, setInvoiceStartDates] = useState([])
    const [invoiceEndDates, setInvoiceEndDates] = useState([])
    const [invoiceExpDates, setInvoiceExpDates] = useState([])
    const [invoiceCreateDates, setInvoiceCreateDates] = useState([])
    const [invoiceAmounts, setInvoiceAmounts] = useState([])

    const [dataMonths, setDataMonths] = useState({})

    useEffect(() => {

        getSettings().then(
            (response) => {
                setClinics(response.data.clinicsSettings.clinics)
                setClinicsKeys(Object.keys(response.data.clinicsSettings.clinics))
                setStartDate(new Date(response.data.year, 1, 1))

                let st = []
                let en = []
                let ex = []
                let cr = []
                let am = []

                response.data.monthlyInvoices.forEach(function (inv, idx) {
                    st[idx] = toDate(inv.startDate)
                    en[idx] = toDate(inv.endDate)
                    ex[idx] = toDate(inv.expirationDate)
                    cr[idx] = toDate(inv.creationDate)
                    am[idx] = idx
                })

                setInvoiceStartDates(st)
                setInvoiceEndDates(en)
                setInvoiceExpDates(ex)
                setInvoiceCreateDates(cr)
                setInvoiceAmounts(am)
            },
            (error) => {
                setErrorMessage(error.response.data)
            })

        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [])

    const toDate = (stringDate) => {
        // expecting YYYY MM dd
        let splits = stringDate.split("-");
        return new Date(+splits[0], +splits[1] - 1, +splits[2])
    }

    const onChangeClinic = (clinic) => {
        setPercent(clinics[clinic].percentage)
        setSelectedClinic(clinic)
    }
    const onChangePercent = (percent) => {
        setPercent(percent)
    }
    const onChangeYear = (date) => {
        setStartYear(date.getFullYear())
        setStartDate(date)

        let newval = [...invoiceStartDates]
        newval.forEach(value => value.setFullYear(date.getFullYear()))
        setInvoiceStartDates(newval)

        newval = [...invoiceEndDates]
        newval.forEach(value => value.setFullYear(date.getFullYear()))
        setInvoiceEndDates(newval)

        newval = [...invoiceExpDates]
        newval.forEach(value => value.setFullYear(date.getFullYear()))
        setInvoiceExpDates(newval)

        newval = [...invoiceCreateDates]
        newval.forEach(value => value.setFullYear(date.getFullYear()))
        setInvoiceCreateDates(newval)

    }

    const setStartDateIdx = (startDate, idx) => {
        let newval = [...invoiceStartDates]
        newval[idx] = startDate;
        setInvoiceStartDates(newval)
    }

    const setEndDateIdx = (endDate, idx) => {
        let newval = [...invoiceEndDates]
        newval[idx] = endDate;
        setInvoiceEndDates(newval)
    }
    const setExpDateIdx = (endDate, idx) => {
        let newval = [...invoiceExpDates]
        newval[idx] = endDate;
        setInvoiceExpDates(newval)
    }
    const setCreateDateIdx = (endDate, idx) => {
        let newval = [...invoiceCreateDates]
        newval[idx] = endDate;
        setInvoiceCreateDates(newval)
    }

    const onChangeAmounts = (amount, idx) => {
        let newval = [...invoiceAmounts]
        newval[idx] = amount;
        setInvoiceAmounts(newval)
    }

    const formatDate = (date) => {
        let y = date.getFullYear()
        let m = date.getMonth()
        let d = date.getDate()

        return y + "-" + m + "-" + d
    }

    const generateReport = (idx) => {

        console.log(invoiceStartDates[0])

        let start = formatDate(invoiceStartDates[idx])
        let end = formatDate(invoiceEndDates[idx])
        let exp = formatDate(invoiceExpDates[idx])
        let created = formatDate(invoiceCreateDates[idx])
        let company = selectedClinic
        let amount = invoiceAmounts[idx]
        let valPercent = percent

        console.log(start)
        console.log(end)
        console.log(exp)
        console.log(created)
        console.log(company)
        console.log(amount)
        console.log(valPercent)


        /**
         * invoiceFromDate=2022-01-01&
         * invoiceToDate=2022-01-31&
         * invoiceCompany=AC_Dental_BV&
         * invoiceAmount=2488.00&
         * invoicePercent=0.5
         * invoiceExpirationDate
         * invoiceCreatedDate
         */

        let query = "invoiceFromDate=" + start + "&invoiceToDate=" + end + "&invoiceCompany=" + company + "&invoiceAmount=" + amount + "&invoicePercent=" + valPercent + "&invoiceExpirationDate=" + exp + "&invoiceCreatedDate=" + created

        getInvoice(query).then(
            (response) => {

            },
            (error) => {
                setErrorMessage(error.response.data)
            })
    }

    return (
        <>

            <Container className="mt-5">
                {errorMessage && (<Alert variant="danger">{errorMessage}</Alert>)}

                <Row>
                    <Col>Invoice For</Col>
                    <Col lg={10}>
                        <Form>
                            <Form.Control as="select" onChange={(e) => onChangeClinic(e.target.value)}>
                                <option>Select the clinic</option>
                                {clinicsKeys.map(function (key, rowIdx) {
                                    return (
                                        <option key={rowIdx} value={key}>{clinics[key].name}</option>
                                    )
                                })}
                            </Form.Control>
                        </Form>

                    </Col>
                </Row>
                <Row className="mt-2">
                    <Col>Percent</Col>
                    <Col>
                        <Form.Group>
                            <Form.Control onChange={(e) => onChangePercent(e.target.value)}
                                          value={percent}/>
                        </Form.Group>
                    </Col>
                </Row>

                <Row className="mt-2">

                    <Col>Invoice Year</Col>
                    <Col lg={10}>
                        <DatePicker selected={startDate}
                                    showYearPicker
                                    dateFormat="yyyy"
                                    onChange={(year) => onChangeYear(year)}/>
                    </Col>
                    <Col>

                    </Col>

                </Row>

                <Row>
                    <Col>month</Col>
                    <Col lg={3}>from</Col>
                    <Col lg={3}>to</Col>
                    <Col>expires</Col>
                    <Col>created</Col>
                    <Col>link</Col>
                </Row>

                {months.map(function (m, idx) {

                    return (

                        <Row className="mt-2" key={idx}>

                            <Col>Invoice {months[idx]}</Col>
                            <Col>
                                <Form.Group>
                                    <Form.Control onChange={(e) => onChangeAmounts(e.target.value)}
                                                  value={invoiceAmounts[idx]}  className="form-control form-control-lg"/>
                                </Form.Group>
                            </Col>
                            <Col>
                                <DatePicker selected={invoiceStartDates[idx]}
                                            dateFormat="d MMM yyyy"
                                            onChange={(date) => setStartDateIdx(date, idx)} className="form-control form-control-lg"/>
                            </Col>
                            <Col>
                                <DatePicker selected={invoiceEndDates[idx]}
                                            dateFormat="d MMM yyyy"
                                            onChange={(date) => setEndDateIdx(date, idx)} className="form-control form-control-lg"/>
                            </Col>
                            <Col>
                                <DatePicker selected={invoiceExpDates[idx]}
                                            dateFormat="d MMM yyyy"
                                            onChange={(date) => setExpDateIdx(date, idx)} className="form-control form-control-lg"/>
                            </Col>
                            <Col>
                                <DatePicker selected={invoiceCreateDates[idx]}
                                            dateFormat="d MMM yyyy"
                                            onChange={(date) => setCreateDateIdx(date, idx)}/>
                            </Col>
                            <Col>
                                <Button variant={"info"} onClick={(idx) => generateReport(idx)}>Download</Button>
                            </Col>
                        </Row>

                    )
                })}

            </Container>

            <Card className="card-default">
                <Card.Header >
                    <h3 className="card-title">Different Width</h3>
                </Card.Header>
                <Card.Body>
                    <Row>
                        <Col xs={12} sm={12} md={6} lg={6} xl={4} className="mt-3">
                            <input type="text" className="form-control form-control-lg" placeholder=".col-3"/>
                        </Col>
                        <Col xs={12} sm={12} md={6} lg={6} xl={4} className="mt-3" >
                            <input type="text" className="form-control form-control-lg" placeholder=".col-4"/>
                        </Col>
                        <Col xs={12} sm={12} md={6} lg={6} xl={4} className="mt-3" >
                            <input type="text" className="form-control form-control-lg" placeholder=".col-5"/>
                        </Col>
                    </Row>
                </Card.Body>
            </Card>

        </>
    );
}

export default App;
