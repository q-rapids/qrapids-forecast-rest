library(elastic)
library(prophet)

stringMethods <- c('PROPHET')
directoryToSave <- "forecastModels"

getAvailableMethods <- function() {
  return(stringMethods)
}

elasticConnection <- function(host, path, user, pwd, port) {
  # CONNECTION TO ELASTICSEARCH NODE
  connect(es_host = host, es_path = path, es_user= user, es_pwd = pwd,
          es_port = port, es_transport_schema  = "http")
  ping()
}

searchElement <- function(name, index, tsfrequency, returnDF) {
  # SEARCH FOR A NORMALIZED ELEMENT AND RETURN THE ASSOCIATED TIME SERIES
  searchString <- ifelse(grepl("metrics", index, fixed=TRUE), 'metric:', 
                         ifelse(grepl("factors", index, fixed=TRUE), 'factor:', 'strategic_indicator:'))
  esearch <- Search(index = index, q = paste(searchString, name, sep = ''),
                    sort = "evaluationDate:asc", source = "value,evaluationDate", size = 10000)$hits$hits
  valuesEsearch <- sapply(esearch, function(x) as.numeric(x$`_source`$value))
  
  if (returnDF == FALSE) {
    timeseries <- ts(valuesEsearch, frequency = tsfrequency, start = 0)
    return(timeseries)
  } else {
    datesEsearch <- sapply(esearch, function(x) as.character(x$`_source`$evaluationDate))
    datesEsearch <- as.Date(datesEsearch)
    df <- data.frame("ds" = datesEsearch, "y" = valuesEsearch)
    return(df)
  }
}

saveModel <- function(name, index, method, model) {
  cleanName <- gsub("[^[:alnum:] ]", "", name)
  dir.create(directoryToSave)
  filename <- paste(cleanName, index, method, sep = '_')
  filename <- paste(directoryToSave, filename, sep = '/')
  saveRDS(model, file = filename)
}

loadModel <- function(name, index, method) {
  cleanName <- gsub("[^[:alnum:] ]", "", name)
  filename <- paste(cleanName, index, method, sep = '_')
  filename <- paste(directoryToSave, filename, sep = '/')
  return(readRDS(filename))
}

checkModelExists <- function(name, index, method) {
  cleanName <- gsub("[^[:alnum:] ]", "", name)
  filename <- paste(cleanName, index, method, sep = '_')
  filename <- paste(directoryToSave, filename, sep = '/')
  return(ifelse(file.exists(filename), TRUE, FALSE))
}

trainProphetModel <- function(name, index) {
  df <- searchElement(name, index, 7, returnDF = TRUE)
  model <- prophet(df, daily.seasonality = 'auto', weekly.seasonality = 'auto')
  saveModel(name, index, stringMethods[1], model)
  return(model)
}

forecastProphet <- function(model, horizon) {
  future <- make_future_dataframe(model, periods = horizon, freq = 'day', include_history = FALSE)
  f <- predict(model, future)
  flist <- list("lower1" = f$yhat_lower, "lower2" = f$yhat_lower, "mean" = f$yhat,
                "upper1" = f$yhat_upper, "upper2" = f$yhat_upper)  
  return(flist)
}

forecastProphetWrapper <- function(name, index, horizon) {
  model <- NULL
  if(checkModelExists(name, index, stringMethods[1])) {
    model <- loadModel(name, index, stringMethods[1])
  } else {
    model <- trainProphetModel(name, index)
  }
  return(forecastProphet(model, horizon))
}