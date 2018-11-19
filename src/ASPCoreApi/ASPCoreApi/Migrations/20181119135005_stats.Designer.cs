﻿// <auto-generated />
using System;
using ASPCoreApi.Models;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Infrastructure;
using Microsoft.EntityFrameworkCore.Metadata;
using Microsoft.EntityFrameworkCore.Migrations;
using Microsoft.EntityFrameworkCore.Storage.ValueConversion;

namespace ASPCoreApi.Migrations
{
    [DbContext(typeof(DatabaseContext))]
    [Migration("20181119135005_stats")]
    partial class stats
    {
        protected override void BuildTargetModel(ModelBuilder modelBuilder)
        {
#pragma warning disable 612, 618
            modelBuilder
                .HasAnnotation("ProductVersion", "2.1.4-rtm-31024")
                .HasAnnotation("Relational:MaxIdentifierLength", 128)
                .HasAnnotation("SqlServer:ValueGenerationStrategy", SqlServerValueGenerationStrategy.IdentityColumn);

            modelBuilder.Entity("ASPCoreApi.Models.Sight", b =>
                {
                    b.Property<int>("id")
                        .ValueGeneratedOnAdd()
                        .HasAnnotation("SqlServer:ValueGenerationStrategy", SqlServerValueGenerationStrategy.IdentityColumn);

                    b.Property<string>("Latitude");

                    b.Property<string>("Longitude");

                    b.Property<string>("Name");

                    b.Property<string>("Website");

                    b.Property<string>("longDescription");

                    b.Property<string>("shortDescription");

                    b.Property<string>("sightImage");

                    b.HasKey("id");

                    b.ToTable("sights");
                });

            modelBuilder.Entity("ASPCoreApi.Models.Statistics", b =>
                {
                    b.Property<int>("StatisticsId")
                        .ValueGeneratedOnAdd()
                        .HasAnnotation("SqlServer:ValueGenerationStrategy", SqlServerValueGenerationStrategy.IdentityColumn);

                    b.Property<int>("highestScore");

                    b.Property<int>("totalFailed");

                    b.Property<int>("totalLost");

                    b.Property<int>("totalScore");

                    b.Property<int>("totalSucces");

                    b.HasKey("StatisticsId");

                    b.ToTable("stats");
                });

            modelBuilder.Entity("ASPCoreApi.Models.Users", b =>
                {
                    b.Property<int>("Id")
                        .ValueGeneratedOnAdd()
                        .HasAnnotation("SqlServer:ValueGenerationStrategy", SqlServerValueGenerationStrategy.IdentityColumn);

                    b.Property<string>("Email");

                    b.Property<string>("FirstName");

                    b.Property<string>("LastName");

                    b.Property<byte[]>("PasswordHash");

                    b.Property<byte[]>("PasswordSalt");

                    b.Property<int?>("StatsStatisticsId");

                    b.Property<string>("Username");

                    b.Property<int>("accessLevel");

                    b.HasKey("Id");

                    b.HasIndex("StatsStatisticsId");

                    b.ToTable("users");
                });

            modelBuilder.Entity("ASPCoreApi.Models.Users", b =>
                {
                    b.HasOne("ASPCoreApi.Models.Statistics", "Stats")
                        .WithMany()
                        .HasForeignKey("StatsStatisticsId");
                });
#pragma warning restore 612, 618
        }
    }
}
