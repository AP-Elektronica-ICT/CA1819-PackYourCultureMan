﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace ASPCoreApi.Models
{
    public class Sight
    {
        public int id { get; set; }
        public string Name { get; set; }
        public string shortDescription { get; set; }
        public string longDescription { get; set; }
        public string Longitude { get; set; }
        public string Latitude { get; set; }
        public string sightImage { get; set; }
        public string Website { get; set; }
    }
}
