/**
 * jqGrid Chinese Translation
 * 咖啡兔 yanhonglei@gmail.com 
 * http://www.kafeitu.me 
 * 
 * 花岗岩 marbleqi@163.com
 * 
 * Dual licensed under the MIT and GPL licenses:
 * http://www.opensource.org/licenses/mit-license.php
 * http://www.gnu.org/licenses/gpl.html 
**/
/*global jQuery, define */
(function( factory ) {
	"use strict";
	if ( typeof define === "function" && define.amd ) {
		// AMD. Register as an anonymous module.
		define([
			"jquery",
			"../grid.base"
		], factory );
	} else {
		// Browser globals
		factory( jQuery );
	}
}(function( $ ) {

$.jgrid = $.jgrid || {};
if(!$.jgrid.hasOwnProperty("regional")) {
	$.jgrid.regional = [];
}
$.jgrid.regional["cn"] = {
    defaults: {
        recordtext: "From {0} to {1} \u3000 total {2} records", // Full-width space before the character '共'
        emptyrecords: "No records!",
        loadtext: "Loading...",
        savetext: "Saving...",
        pgtext: "Page {0} \u3000 total {1} pages",
        pgfirst: "First page",
        pglast: "Last page",
        pgnext: "Next page",
        pgprev: "Previous page",
        pgrecs: "Records per page",
        showhide: "Toggle Expand Collapse Table",
        // mobile
        pagerCaption: "Table::Page Settings",
        pageText: "Page:",
        recordPage: "Records per page",
        nomorerecs: "No more records...",
        scrollPullup: "Load more...",
        scrollPulldown: "Refresh...",
        scrollRefresh: "Scroll to refresh..."
    },
    search: {
        caption: "Search...",
        Find: "Find",
        Reset: "Reset",
        odata: [
            { oper:'eq', text:'Equal \u3000\u3000'},
            { oper:'ne', text:'Not equal \u3000'},
            { oper:'lt', text:'Less than \u3000\u3000'},
            { oper:'le', text:'Less than or equal'},
            { oper:'gt', text:'Greater than \u3000\u3000'},
            { oper:'ge', text:'Greater than or equal'},
            { oper:'bw', text:'Starts with'},
            { oper:'bn', text:'Does not start with'},
            { oper:'in', text:'In \u3000\u3000'},
            { oper:'ni', text:'Not in'},
            { oper:'ew', text:'Ends with'},
            { oper:'en', text:'Does not end with'},
            { oper:'cn', text:'Contains \u3000\u3000'},
            { oper:'nc', text:'Does not contain'},
            { oper:'nu', text:'Is null'},
            { oper:'nn', text:'Is not null'},
            { oper:'bt', text:'Between'}
        ],
        groupOps: [
            { op: "AND", text: "Match all conditions" },
            { op: "OR", text: "Match any condition" }
        ],
        operandTitle: "Click to search.",
        resetTitle: "Reset search conditions",
        addsubgrup: "Add condition group",
        addrule: "Add condition",
        delgroup: "Delete condition group",
        delrule: "Delete condition",
        Close: "Close",
        Operand: "Operand: ",
        Operation: "Operation: "
    },
    edit: {
        addCaption: "Add record",
        editCaption: "Edit record",
        bSubmit: "Submit",
        bCancel: "Cancel",
        bClose: "Close",
        saveData: "Data has changed, save?",
        bYes: "Yes",
        bNo: "No",
        bExit: "Cancel",
        msg: {
            required: "This field is required",
            number: "Please enter a valid number",
            minValue: "Value must be greater than or equal to ",
            maxValue: "Value must be less than or equal to ",
            email: "This is not a valid email address",
            integer: "Please enter a valid integer",
            date: "Please enter a valid date",
            url: "Invalid URL. Prefix must be ('http://' or 'https://')",
            nodefined: "Not defined!",
            novalue: "No return value!",
            customarray: "Custom function should return array!",
            customfcheck: "Custom function should be present!"
        }
    },
    view: {
        caption: "View record",
        bClose: "Close"
    },
    del: {
        caption: "Delete",
        msg: "Delete selected record?",
        bSubmit: "Delete",
        bCancel: "Cancel"
    },
    nav: {
        edittext: "",
        edittitle: "Edit selected record",
        addtext: "",
        addtitle: "Add new record",
        deltext: "",
        deltitle: "Delete selected record",
        searchtext: "",
        searchtitle: "Search",
        refreshtext: "",
        refreshtitle: "Refresh table",
        alertcap: "Attention",
        alerttext: "Please select a record",
        viewtext: "",
        viewtitle: "View selected record",
        savetext: "",
        savetitle: "Save record",
        canceltext: "",
        canceltitle: "Cancel editing record",
        selectcaption: "Action..."
    },
    col: {
        caption: "Select columns",
        bSubmit: "Confirm",
        bCancel: "Cancel"
    },
    errors: {
        errcap: "Error",
        nourl: "URL not set",
        norecords: "No records to process",
        model: "colNames and colModel length mismatch!"
    },
    formatter : {
        integer : {thousandsSeparator: ",", defaultValue: '0'},
        number : {decimalSeparator:".", thousandsSeparator: ",", decimalPlaces: 2, defaultValue: '0.00'},
        currency : {decimalSeparator:".", thousandsSeparator: ",", decimalPlaces: 2, prefix: "", suffix:"", defaultValue: '0.00'},
        date : {
            dayNames: [
                "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat",
                "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"
            ],
            monthNames: [
                "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec",
                "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"
            ],
            AmPm: ["am", "pm", "AM", "PM"],
            S: function (j) {return j < 11 || j > 13 ? ['st', 'nd', 'rd', 'th'][Math.min((j - 1) % 10, 3)] : 'th';},
            srcformat: 'Y-m-d',
            newformat: 'Y-m-d',
            parseRe : /[#%\\\/:_;.,\t\s-]/,
            masks : {
                // see http://php.net/manual/en/function.date.php for PHP format used in jqGrid
                // and see http://docs.jquery.com/UI/Datepicker/formatDate
                // and https://github.com/jquery/globalize#dates for alternative formats used frequently
                // one can find on https://github.com/jquery/globalize/tree/master/lib/cultures many
                // information about date, time, numbers and currency formats used in different countries
                // one should just convert the information in PHP format
                ISO8601Long:"Y-m-d H:i:s",
                ISO8601Short:"Y-m-d",
                // short date:
                //    n - Numeric representation of a month, without leading zeros
                //    j - Day of the month without leading zeros
                //    Y - A full numeric representation of a year, 4 digits
                // example: 3/1/2012 which means 1 March 2012
                ShortDate: "n/j/Y", // in jQuery UI Datepicker: "M/d/yyyy"
                // long date:
                //    l - A full textual representation of the day of the week
                //    F - A full textual representation of a month
                //    d - Day of the month, 2 digits with leading zeros
                //    Y - A full numeric representation of a year, 4 digits
                LongDate: "l, F d, Y", // in jQuery UI Datepicker: "dddd, MMMM dd, yyyy"
                // long date with long time:
                //    l - A full textual representation of the day of the week
                //    F - A full textual representation of a month
                //    d - Day of the month, 2 digits with leading zeros
                //    Y - A full numeric representation of a year, 4 digits
                //    g - 12-hour format of an hour without leading zeros
                //    i - Minutes with leading zeros
                //    s - Seconds, with leading zeros
                //    A - Uppercase Ante meridiem and Post meridiem (AM or PM)
                FullDateTime: "l, F d, Y g:i:s A", // in jQuery UI Datepicker: "dddd, MMMM dd, yyyy h:mm:ss tt"
                // month day:
                //    F - A full textual representation of a month
                //    d - Day of the month, 2 digits with leading zeros
                MonthDay: "F d", // in jQuery UI Datepicker: "MMMM dd"
                // short time (without seconds)
                //    g - 12-hour format of an hour without leading zeros
                //    i - Minutes with leading zeros
                //    A - Uppercase Ante meridiem and Post meridiem (AM or PM)
                ShortTime: "g:i A", // in jQuery UI Datepicker: "h:mm tt"
                // long time (with seconds)
                //    g - 12-hour format of an hour without leading zeros
                //    i - Minutes with leading zeros
                //    s - Seconds, with leading zeros
                //    A - Uppercase Ante meridiem and Post meridiem (AM or PM)
                LongTime: "g:i:s A", // in jQuery UI Datepicker: "h:mm:ss tt"
                SortableDateTime: "Y-m-d\\TH:i:s",
                UniversalSortableDateTime: "Y-m-d H:i:sO",
                // month with year
                //    Y - A full numeric representation of a year, 4 digits
                //    F - A full textual representation of a month
                YearMonth: "F, Y" // in jQuery UI Datepicker: "MMMM, yyyy"
            },
            reformatAfterEdit : false,
			userLocalTime : false
        },
        baseLinkUrl: '',
        showAction: '',
        target: '',
        checkbox : {disabled:true},
        idName : 'id'
    },
	colmenu : {
		sortasc: "Sort Ascending",
        sortdesc: "Sort Descending",
        columns: "Columns",
        filter: "Filter",
        grouping: "Group",
        ungrouping: "Ungroup",
        searchTitle: "Search:",
        freeze: "Freeze",
        unfreeze: "Unfreeze",
        reorder: "Reorder",
        hovermenu: "Click for column quick actions"
	}
};
}));
