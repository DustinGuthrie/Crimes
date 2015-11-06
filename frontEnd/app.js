$(document).ready(function(){
  statsPage.init();
});


var statsPage = {
  init: function(){
      statsPage.initStyling();
      statsPage.initEvents();
    },
  initStyling: function(){
    statsPage.grabStatsFromServer();

  },
  initEvents: function(){
    //*****Login page that will then bring up Home page - THECLICKHIDDENFUNCTION****
    $('#stateYearButton').on('click', function(event){
      console.log("this is happening - initialbutton");
      event.preventDefault();
      $('.statMain').toggleClass('hidden');
      var $state = $('input[name="state"]').val();
      var $year = $('input[year="year"]').val();
      // statsPage.setState($state);
      // statsPage.setYear($year);
      _.each(stats, function(currVal, idx, array) {
        if($state === currVal.state && $year === currVal.year) {
          statsPage.loadStats(currVal);
        } else {
          statsPage.loadStats();
        }
      }
    });
  },
},

  grabStatsFromServer: function() {
    $.ajax({
      type: 'GET',
      url: statsPage.url,
      success: function(data) {
        console.log("SUCCESS: ", data);
        statsPage.loadStats(data);
      },
      failure: function(data) {
        console.log("FAILURE: ", data);
      }
    });
  },

  //once we GET chats we have to load into template here
  loadStats: function(data) {
    var statsHTML = "";
        var statsTemplateCurrUser = _.template($('#statsTmplCurrUser').html());
        statsHTML += statsTemplateCurrUser(currVal);
        $('.statMain').html(statsHTML);
  },


  url: "/home",

};
};
