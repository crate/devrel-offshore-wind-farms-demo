package main

import (
	"context"
	"fmt"
	"log"
	"os"

	"github.com/gofiber/fiber/v2"
	"github.com/jackc/pgx/v5"
	"github.com/jackc/pgx/v5/pgxpool"
	"github.com/joho/godotenv"
)

func main() {
	err := godotenv.Load()
	if err != nil {
		log.Fatal("Error loading .env file!")
	}

	// Connect to CrateDB.
	dbpool, err := pgxpool.New(context.Background(), os.Getenv("CRATEDB_URL"))
	if err != nil {
		log.Fatalf("Error connecting to CrateDB: %v", err)
	}
	defer dbpool.Close()

	app := fiber.New()
	app.Static("/", "./public")

	app.Get("/api/windfarms", func(c *fiber.Ctx) error {
		type windfarm struct {
			Id          string `json:"id"`
			Name        string `json:"name"`
			Description string `json:"description"`
			Location    struct {
				X float64 `json:"x"`
				Y float64 `json:"y"`
			} `json:"location"`
			Boundaries any `json:"boundaries"`
			Turbines   any `json:"turbines"`
		}

		conn, err := dbpool.Acquire(context.Background())

		if err != nil {
			log.Fatalf("Error acquiring connection: %v", err)
		}
		defer conn.Release()

		rows, err := conn.Query(context.Background(), "SELECT id, name, description, longitude(location) as x, latitude(location) as y, boundaries, turbines FROM windfarms ORDER BY id ASC")
		if err != nil {
			log.Fatalf("Error running query: %v", err)
		}
		defer rows.Close()

		windfarms := []windfarm{}

		// TODO what if there are 0 results?

		for rows.Next() {
			windfarm := windfarm{}
			err := rows.Scan(&windfarm.Id, &windfarm.Name, &windfarm.Description, &windfarm.Location.X, &windfarm.Location.Y, &windfarm.Boundaries, &windfarm.Turbines)

			if err != nil {
				log.Fatalf("Error scanning rows: %v", err)
			}

			windfarms = append(windfarms, windfarm)
		}

		return c.JSON(&fiber.Map{
			"results": windfarms,
		})
	})

	app.Get("/api/latest/:id", func(c *fiber.Ctx) error {
		type result struct {
			Timestamp        int64   `json:"timestamp"`
			Day              int64   `json:"day"`
			Month            int64   `json:"month"`
			Output           float32 `json:"output"`
			OutputPercentage float32 `json:"outputPercentage"`
		}

		conn, err := dbpool.Acquire(context.Background())

		if err != nil {
			log.Fatalf("Error acquiring connection: %v", err)
		}
		defer conn.Release()

		rows, err := conn.Query(context.Background(), "SELECT ts::long AS timestamp, day::long as day, month::long as month, output, outputpercentage FROM windfarm_output WHERE windfarmid = $1 ORDER BY ts DESC LIMIT 1", c.Params("id"))
		if err != nil {
			log.Fatalf("Error running query: %v", err)
		}

		results, err := pgx.CollectRows(rows, pgx.RowToStructByName[result])
		if err != nil {
			log.Fatalf("Error collecting rows: %v", err)
		}

		return c.JSON(&fiber.Map{
			"results": results,
		})
	})

	app.Get("/api/avgpctformonth/:id/:ts", func(c *fiber.Ctx) error {
		type result struct {
			AvgPct float32 `json:"avgPct" db:"avgoutputpct"`
		}

		conn, err := dbpool.Acquire(context.Background())

		if err != nil {
			log.Fatalf("Error acquiring connection: %v", err)
		}
		defer conn.Release()

		rows, err := conn.Query(context.Background(), "SELECT trunc(avg(outputpercentage), 2) AS avgoutputpct FROM windfarm_output WHERE windfarmid = $1 and month = $2", c.Params("id"), c.Params("ts"))
		if err != nil {
			log.Fatalf("Error running query: %v", err)
		}

		// TODO deal with 0 results.

		results, err := pgx.CollectRows(rows, pgx.RowToStructByName[result])
		if err != nil {
			log.Fatalf("Error collecting rows: %v", err)
		}

		return c.JSON(&fiber.Map{
			"results": results,
		})
	})

	app.Get("/api/outputforday/:id/:ts", func(c *fiber.Ctx) error {
		type result struct {
			Hour             int     `json:"hour"`
			Output           float32 `json:"output"`
			CumulativeOutput float32 `json:"cumulativeOutput"`
		}

		conn, err := dbpool.Acquire(context.Background())

		if err != nil {
			log.Fatalf("Error acquiring connection: %v", err)
		}
		defer conn.Release()

		rows, err := conn.Query(context.Background(), "SELECT extract(hour from ts) AS hour, output, sum(output) OVER (ORDER BY ts ASC) AS cumulativeoutput FROM windfarm_output WHERE windfarmid = $1 AND day = $2 ORDER BY hour ASC", c.Params("id"), c.Params("ts"))
		if err != nil {
			log.Fatalf("Error running query: %v", err)
		}

		// TODO deal with 0 results.

		results, err := pgx.CollectRows(rows, pgx.RowToStructByName[result])
		if err != nil {
			log.Fatalf("Error collecting rows: %v", err)
		}

		return c.JSON(&fiber.Map{
			"results": results,
		})
	})

	app.Get("/api/dailymaxpct/:id/:days", func(c *fiber.Ctx) error {
		// "SELECT day::long as day, max(outputpercentage) AS maxoutputpercentage FROM windfarm_output WHERE windfarmid = $1 GROUP BY day ORDER BY day DESC LIMIT $2"

		conn, err := dbpool.Acquire(context.Background())

		if err != nil {
			log.Fatalf("Error acquiring connection: %v", err)
		}
		defer conn.Release()

		return c.JSON(&fiber.Map{
			"results": nil,
		})
	})

	app.Listen(fmt.Sprintf(":%s", os.Getenv("PORT")))
}
