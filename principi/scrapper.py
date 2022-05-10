#!/usr/share/env python3

# TODO:
# [-] add function for sending data to the database (POST data to our API)
# [-] add error handling

import requests
from bs4 import BeautifulSoup, Comment
from dataclasses import dataclass
import re
import json
import csv
import argparse
from time import sleep


@dataclass
class Station:
    title: str
    geopos: tuple
    url: str

@dataclass
class Pollutant:
    name: str
    unit: str
    value: int

@dataclass
class Allergen:
    name: str
    value: float


# Know when to use it and don't abuse it!
def toJSON(obj):
    return json.dumps(obj.__dict__)


def getStationInfo(source: str):
    response = requests.get(source)
    soup = BeautifulSoup(response.text, "html.parser")  # maybe switch to "lxml" parser(should be faster, don't know if that's really needed)

    pollutants = soup.find("div", class_="pollutants")
    allergens = soup.find("div", class_="allergens")

    pollutantList = []
    for item in pollutants.find_all("div", class_="pollutant-item"):
        name = item.find("div", class_="name").text
        unit = item.find("div", class_="unit").text
        value = item.find("div", class_="value").text
        pollutantList.append(Pollutant(name, unit, value))
    # print(pollutantList)    # DEBUGGING

    allergenList = []
    for item in allergens.find_all("div", class_="pollutant-item"):
        name = item.find("div", class_="name").text
        value = item.find("div", class_="value").text
        allergenList.append(Allergen(name, value))
    # print(allergenList)    # DEBUGGING

    # UPDATE TIME
    updateTime = soup.find("div", class_="update-time").text
    # print(f"Update-time: {updateTime}") # DEBUGGING

    # INDEX VALUE (AQI)
    comments = soup.find_all(text=lambda text: isinstance(text, Comment))
    comments = ''.join(comments)
    indexValue = re.search('<div class="indexValue">(\d+)<\/div>', comments).groups()[0]
    # print(f"Index value: {indexValue}") # DEBUGGING

    # TITLE
    detail = soup.find("div", class_="detail-title")
    title = detail.h2.text
    location = detail.p.text
    # print(f"Title: {title}, location: {location}")  # DEBUGGING

    return (title, location, updateTime, indexValue, pollutantList, allergenList)


# Return a list of stations read from .csv file
def parseStations(filename):
    stations = []
    with open(filename) as fin:
        reader = csv.reader(fin, delimiter=',')
        for row in reader:
            station = Station(row[0], (row[1],row[2]), row[3])
            stations.append(station)
    return stations


# maybe have a default -s -i and -u settings?
def arguments():
    parser = argparse.ArgumentParser(
        description="Air-quality scrapper for pollutant and allergen information.",
        epilog="EXAMPLE: ./scrapper.py -s stations.csv -i 5 -u minutes --token <token>"
    )
    parser.add_argument("-v", "--verbose", help="Verbose output", action='store_true')
    parser.add_argument("-s", "--stations", help="Path for the stations.csv file", type=str, required=True)
    parser.add_argument("-i", "--interval", help="Scrapping frequency interval", type=int, required=True)
    parser.add_argument("-u", "--unit", help="Unit for the scrapping frequency interval", choices=["seconds", "minutes", "hours", "days"], required=True)
    parser.add_argument("-t", "--token", help="JWT auth token for accessing the API", type=str, required=True)
    args = parser.parse_args()
    return args


def main():
    stations = parseStations(ARGS.stations)
    match ARGS.unit:
        case "seconds":
            INTERVAL = ARGS.interval
        case "minutes":
            INTERVAL = ARGS.interval * 60
        case "hours":
            INTERVAL = ARGS.interval * 3600
        case "days":
            INTERVAL = ARGS.interval * 86400

    # while True:
    #     sleep(INTERVAL)
    #     for s in stations:
    #         print(getStationInfo(s.url))


if __name__ == "__main__":
    ARGS = arguments()
    main()
